package org.sayandev.stickynote.core.messaging.websocket

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake
import org.java_websocket.server.WebSocketServer
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.PayloadBehaviour
import org.sayandev.stickynote.core.messaging.PayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.Publisher
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.math.min

open class WebSocketPublisher<P : Any, R : Any>(
    messageMeta: MessageMeta<P, R>,
    connectionMeta: WebSocketConnectionMeta,
    logger: Logger,
) : Publisher<WebSocketConnectionMeta, P, R>(messageMeta, connectionMeta, logger) {

    private var client: WebSocketClient? = null
    private var server: WebSocketServer? = null
    private val connectLock = Any()
    @Volatile private var openSignal: CompletableDeferred<Unit>? = null

    // Rate-limit connection error logs to avoid spam
    private val logWindowMs = 3000L
    @Volatile private var lastConnLog = 0L

    // Cooldown to avoid hammering connect attempts when the target is down
    private val retryCooldownMs = 5000L
    @Volatile private var nextRetryAt = 0L

    companion object {
        // Single embedded server per port inside this JVM
        private val SERVER_REGISTRY = ConcurrentHashMap<Int, WebSocketServer>()

        // Improved port check: try active connect; if no listener, try bind with SO_REUSEADDR.
        private fun isPortFree(bindHost: String, port: Int): Boolean {
            // If someone is listening, a connect will succeed -> not free
            try { Socket("127.0.0.1", port).use { return false } } catch (_: Exception) { /* no listener */ }
            return try {
                ServerSocket().use { s ->
                    s.reuseAddress = true
                    s.bind(InetSocketAddress(bindHost, port))
                    true
                }
            } catch (_: Exception) {
                false
            }
        }
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread { closeQuietly() })
        connectOrStartServerBlocking()
    }

    private fun infoRateLimited(msg: String) {
        val now = System.currentTimeMillis()
        if (now - lastConnLog >= logWindowMs) {
            lastConnLog = now
            logger.info(msg)
        }
    }

    private fun warnRateLimited(msg: String) {
        val now = System.currentTimeMillis()
        if (now - lastConnLog >= logWindowMs) {
            lastConnLog = now
            logger.warning(msg)
        }
    }

    private fun buildClient(uri: URI): WebSocketClient {
        val signal = CompletableDeferred<Unit>().also { openSignal = it }
        return object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
//                logger.info("Connected to WebSocket server ${uri.host}:${uri.port}")
                if (!signal.isCompleted) signal.complete(Unit)
                this.setConnectionLostTimeout(10)
            }
            override fun onMessage(message: String?) {
                if (message != null) handleMessage(message)
            }
            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                // Rate-limited to avoid spam during reconnect
//                infoRateLimited("WebSocket closed (${uri.host}:${uri.port}): $reason")
            }
            override fun onError(ex: Exception) {
                // Rate-limited to avoid spam during reconnect
//                warnRateLimited("WebSocket error (${uri.host}:${uri.port}): ${ex.message}")
            }
        }
    }

    // Start an embedded server locally; reuse if already running; enable SO_REUSEADDR.
    private fun startServer(bindHost: String, port: Int) {
        SERVER_REGISTRY[port]?.let {
            server = it
            logger.info("Reusing existing WS server on $bindHost:$port")
            return
        }
        if (!isPortFree(bindHost, port)) {
            logger.info("WS server port $port is busy; skipping embedded server start.")
            return
        }
        val srv = object : WebSocketServer(InetSocketAddress(bindHost, port)) {
            override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {
                logger.info("WS client connected: ${conn.remoteSocketAddress}")
            }
            override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {
                logger.info("WS client disconnected: ${conn.remoteSocketAddress}")
            }
            override fun onMessage(conn: WebSocket, message: String?) {
                if (message != null) handleMessage(message)
            }
            override fun onError(conn: WebSocket?, ex: Exception) {
                logger.warning("WS server error: ${ex.message}")
            }
            override fun onStart() {
                logger.info("WS server started on $address")
            }
        }.apply {
            @Suppress("DEPRECATION")
            this.isReuseAddr = true
        }
        try {
            srv.start()
            SERVER_REGISTRY[port] = srv
            server = srv
        } catch (e: Exception) {
            logger.warning("Failed to start WS server on $bindHost:$port: ${e.message}")
        }
    }

    private fun closeQuietly() {
        try {
            client?.let {
                try { if (it.isOpen || it.isClosing) it.closeBlocking() else it.close() } catch (_: Exception) {}
            }
        } finally { client = null }
        try {
            server?.let {
                try { it.stop(1000) } catch (_: Exception) {}
                SERVER_REGISTRY.entries.removeIf { e -> e.value === server }
            }
        } finally { server = null }
    }

    private fun replaceClient(newUri: URI): Boolean {
        client?.let {
            try { if (it.isOpen || it.isClosing) it.closeBlocking() else it.close() } catch (_: Exception) {}
        }
        val c = buildClient(newUri)
        client = c
        return try {
            c.connectBlocking(1500, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            infoRateLimited("Connection failed ($newUri): ${e.message}")
            false
        }
    }

    // Retry connect with exponential backoff; optionally wait for onOpen.
    private fun connectWithRetry(uri: URI, maxAttempts: Int = 6, maxBackoffMs: Long = 2000): Boolean {
        logger.info("Trying to open a new websocket connection on ${uri.host}:${uri.port}")
        var attempt = 0
        while (attempt < maxAttempts) {
            attempt++
            if (replaceClient(uri)) {
                val ok = runCatching {
                    runBlocking {
                        withTimeoutOrNull(500) { openSignal?.await() } != null && (client?.isOpen == true)
                    }
                }.getOrDefault(false)
                if (ok) return true
            }
            val delayMs = min(150L * (1 shl (attempt - 1)), maxBackoffMs)
            try { Thread.sleep(delayMs) } catch (_: InterruptedException) {}
        }
        return false
    }

    // Single-flight connect: apply cooldown after a full failure to avoid log spam.
    private fun connectOrStartServerBlocking(): Boolean {
        val now = System.currentTimeMillis()
        if (now < nextRetryAt) return false

        val targetUri = connectionMeta.uri
        synchronized(connectLock) {
            client?.let { if (it.isOpen) { nextRetryAt = 0; return true } }

            // 1) Connect to target with retry/backoff
            if (connectWithRetry(targetUri)) { nextRetryAt = 0; return true }

            // 2) Start embedded server if possible; then connect to local fallback
            startServer("0.0.0.0", targetUri.port)
            try { Thread.sleep(200) } catch (_: InterruptedException) {}
            val localFallback = URI("${targetUri.scheme}://127.0.0.1:${targetUri.port}${targetUri.rawPath ?: ""}")
            val ok = connectWithRetry(localFallback)
            nextRetryAt = if (ok) 0 else System.currentTimeMillis() + retryCooldownMs
            return ok
        }
    }

    private fun handleMessage(message: String) {
        val result = message.asPayloadWrapper<R>()
        when (result.behaviour) {
            PayloadBehaviour.FORWARD -> {
                val wrappedPayload = message.asPayloadWrapper<P>()
                if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return
                val payloadResult = handle(wrappedPayload.typedPayload(messageMeta.payloadType)) ?: return
                client?.takeIf { it.isOpen }?.send(
                    PayloadWrapper(
                        wrappedPayload.uniqueId,
                        payloadResult,
                        PayloadBehaviour.RESPONSE,
                        wrappedPayload.source,
                        wrappedPayload.target,
                        wrappedPayload.excludeSource
                    ).asJson()
                )
            }
            PayloadBehaviour.RESPONSE -> {
                for (publisher in HANDLER_LIST.filterIsInstance<WebSocketPublisher<P, R>>()) {
                    if (publisher.messageMeta.id() == messageMeta.id()) {
                        publisher.payloads[result.uniqueId]?.apply {
                            this.complete(result.typedPayload(messageMeta.resultType))
                            publisher.payloads.remove(result.uniqueId)
                        }
                    }
                }
            }
            PayloadBehaviour.FORWARD_PROXY -> {}
        }
    }

    override suspend fun publish(payload: PayloadWrapper<P>): CompletableDeferred<R> {
        if (!HANDLER_LIST.contains(this)) {
            throw IllegalStateException("Publisher with id ${messageMeta.id()} is not registered")
        }

        val result = super.publish(payload)

        // Timeout guard for response
        launch(this@WebSocketPublisher.connectionMeta.dispatcher) {
            delay(connectionMeta.timeoutMillis)
            if (result.isActive) {
                result.completeExceptionally(
                    IllegalStateException(
                        "Sent payload has not been responded in ${connectionMeta.timeoutMillis}ms. Payload: $payload (id: ${messageMeta.id()})"
                    )
                )
            }
            payloads.remove(payload.uniqueId)
        }

        // Safe send with connect/retry and cooldown
        launch(this@WebSocketPublisher.connectionMeta.dispatcher) {
            val connected = connectOrStartServerBlocking()
            val ws = client
            if (!connected || ws == null || !ws.isOpen) {
                result.completeExceptionally(
                    IllegalStateException("WebSocket is not connected to ${connectionMeta.uri}. Payload not sent.")
                )
                payloads.remove(payload.uniqueId)
                return@launch
            }
            try {
                withTimeoutOrNull(500) { openSignal?.await() }
                ws.send(payload.asJson())
            } catch (e: Exception) {
                result.completeExceptionally(e)
                payloads.remove(payload.uniqueId)
            }
        }

        return result
    }
}