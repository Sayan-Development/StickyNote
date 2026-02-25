package org.sayandev.stickynote.core.messaging.websocket

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger
import kotlin.math.min

open class WebSocketPublisher<P : Any, R : Any>(
    messageMeta: MessageMeta<P, R>,
    connectionMeta: WebSocketConnectionMeta,
    logger: Logger,
) : Publisher<WebSocketConnectionMeta, P, R>(messageMeta, connectionMeta, logger) {

    private val endpoint = acquireEndpoint()
    @Volatile private var closed = false

    companion object {
        private const val LOG_WINDOW_MS = 3000L
        private const val RETRY_COOLDOWN_MS = 5000L

        private data class EndpointKey(
            val uri: URI,
            val autoHostOnPort: Boolean,
            val hostWhenAutoHosting: String,
        )

        private data class ServerLease(
            val server: WebSocketServer,
            val owners: AtomicInteger = AtomicInteger(1),
        )

        private class SharedEndpoint(
            val key: EndpointKey,
            val dispatcher: CoroutineDispatcher,
            val logger: Logger,
        ) {
            @Volatile var client: WebSocketClient? = null
            val connectLock = Any()
            @Volatile var openSignal: CompletableDeferred<Unit>? = null
            @Volatile var shuttingDown = false
            @Volatile var suppressReconnectEvents = false
            @Volatile var waitingForServer = false
            @Volatile var autoHostAnnounced = false
            @Volatile var leasedServerPort: Int? = null
            @Volatile var lastConnLog = 0L
            @Volatile var nextRetryAt = 0L
            val reconnectScheduled = AtomicBoolean(false)
            val owners = AtomicInteger(1)
            val publishers: MutableSet<WebSocketPublisher<*, *>> = ConcurrentHashMap.newKeySet()
        }

        // One shared client per URI/options inside this JVM.
        private val ENDPOINT_REGISTRY = ConcurrentHashMap<EndpointKey, SharedEndpoint>()

        // Single embedded server per port inside this JVM.
        private val SERVER_REGISTRY = ConcurrentHashMap<Int, ServerLease>()

        // Improved port check: try active connect; if no listener, try bind with SO_REUSEADDR.
        private fun isPortFree(bindHost: String, port: Int): Boolean {
            try { Socket("127.0.0.1", port).use { return false } } catch (_: Exception) { }
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
        connectOrStartServerBlocking(endpoint)
    }

    private fun acquireEndpoint(): SharedEndpoint {
        val key = EndpointKey(
            uri = connectionMeta.uri,
            autoHostOnPort = connectionMeta.autoHostOnPort,
            hostWhenAutoHosting = connectionMeta.hostWhenAutoHosting
        )

        return ENDPOINT_REGISTRY.compute(key) { _, existing ->
            if (existing != null) {
                existing.owners.incrementAndGet()
                existing.publishers.add(this)
                existing
            } else {
                SharedEndpoint(key, connectionMeta.dispatcher, logger).apply {
                    publishers.add(this@WebSocketPublisher)
                }
            }
        }!!
    }

    private fun releaseEndpoint() {
        endpoint.publishers.remove(this)
        if (endpoint.owners.decrementAndGet() > 0) return

        endpoint.shuttingDown = true
        try {
            endpoint.client?.let {
                endpoint.suppressReconnectEvents = true
                try { if (it.isOpen || it.isClosing) it.closeBlocking() else it.close() } catch (_: Exception) {}
                finally { endpoint.suppressReconnectEvents = false }
            }
        } finally {
            endpoint.client = null
        }

        releaseServerLease(endpoint)
        ENDPOINT_REGISTRY.remove(endpoint.key, endpoint)
    }

    private fun infoRateLimited(endpoint: SharedEndpoint, msg: String) {
        val now = System.currentTimeMillis()
        if (now - endpoint.lastConnLog >= LOG_WINDOW_MS) {
            endpoint.lastConnLog = now
            endpoint.logger.info(msg)
        }
    }

    private fun warnRateLimited(endpoint: SharedEndpoint, msg: String) {
        val now = System.currentTimeMillis()
        if (now - endpoint.lastConnLog >= LOG_WINDOW_MS) {
            endpoint.lastConnLog = now
            endpoint.logger.warning(msg)
        }
    }

    private fun isConnectionRefused(ex: Exception): Boolean {
        var current: Throwable? = ex
        while (current != null) {
            val message = current.message?.lowercase().orEmpty()
            if ("connection refused" in message) {
                return true
            }
            current = current.cause
        }
        return false
    }

    private fun dispatchInbound(endpoint: SharedEndpoint, message: String) {
        endpoint.publishers.forEach { it.handleMessage(message) }
    }

    private fun buildClient(endpoint: SharedEndpoint, uri: URI): WebSocketClient {
        val signal = CompletableDeferred<Unit>().also { endpoint.openSignal = it }
        return object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                if (!signal.isCompleted) signal.complete(Unit)
                this.setConnectionLostTimeout(10)
                endpoint.waitingForServer = false
                endpoint.autoHostAnnounced = false
                infoRateLimited(endpoint, "Connected to WebSocket server ${uri.host}:${uri.port}")
            }

            override fun onMessage(message: String?) {
                if (message != null) dispatchInbound(endpoint, message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                if (!endpoint.shuttingDown) {
                    if (!endpoint.waitingForServer) {
                        endpoint.logger.info("WebSocket disconnected from ${uri.host}:${uri.port}; reconnecting...")
                    }
                    endpoint.waitingForServer = true
                    scheduleReconnect(endpoint)
                }
            }

            override fun onError(ex: Exception) {
                if (!endpoint.shuttingDown) {
                    if (isConnectionRefused(ex)) {
                        endpoint.waitingForServer = true
                    } else {
                        warnRateLimited(endpoint, "WebSocket error (${uri.host}:${uri.port}): ${ex.message}")
                    }
                    scheduleReconnect(endpoint)
                }
            }
        }
    }

    private fun scheduleReconnect(endpoint: SharedEndpoint) {
        if (endpoint.shuttingDown || endpoint.suppressReconnectEvents) return
        if (!endpoint.reconnectScheduled.compareAndSet(false, true)) return

        launch(endpoint.dispatcher) {
            try {
                connectOrStartServerBlocking(endpoint)
            } finally {
                endpoint.reconnectScheduled.set(false)
            }
        }
    }

    // Start/reuse embedded server on the configured port.
    private fun startServer(endpoint: SharedEndpoint, bindHost: String, port: Int) {
        if (endpoint.leasedServerPort == port) return

        synchronized(SERVER_REGISTRY) {
            SERVER_REGISTRY[port]?.let { lease ->
                lease.owners.incrementAndGet()
                endpoint.leasedServerPort = port
                return
            }

            if (!isPortFree(bindHost, port)) {
                return
            }

            val srv = object : WebSocketServer(InetSocketAddress(bindHost, port)) {
                override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {}
                override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {}
                override fun onMessage(conn: WebSocket, message: String?) {
                    if (message != null) {
                        broadcast(message)
                    }
                }
                override fun onError(conn: WebSocket?, ex: Exception) {
                    endpoint.logger.warning("WS server error: ${ex.message}")
                }
                override fun onStart() {
                    endpoint.logger.info("WS server started on $address")
                }
            }.apply {
                @Suppress("DEPRECATION")
                this.isReuseAddr = true
            }

            try {
                srv.start()
                SERVER_REGISTRY[port] = ServerLease(srv)
                endpoint.leasedServerPort = port
            } catch (e: Exception) {
                endpoint.logger.warning("Failed to start WS server on $bindHost:$port: ${e.message}")
            }
        }
    }

    private fun releaseServerLease(endpoint: SharedEndpoint) {
        val port = endpoint.leasedServerPort ?: return
        synchronized(SERVER_REGISTRY) {
            val lease = SERVER_REGISTRY[port]
            endpoint.leasedServerPort = null
            if (lease == null) return
            if (lease.owners.decrementAndGet() > 0) return

            SERVER_REGISTRY.remove(port, lease)
            try {
                lease.server.stop(1000)
            } catch (_: Exception) {
                // ignored
            }
        }
    }

    private fun replaceClient(endpoint: SharedEndpoint, newUri: URI): Boolean {
        endpoint.client?.let {
            endpoint.suppressReconnectEvents = true
            try { if (it.isOpen || it.isClosing) it.closeBlocking() else it.close() } catch (_: Exception) {}
            finally { endpoint.suppressReconnectEvents = false }
        }

        val newClient = buildClient(endpoint, newUri)
        endpoint.client = newClient
        return try {
            newClient.connectBlocking(1500, TimeUnit.MILLISECONDS)
        } catch (_: Exception) {
            false
        }
    }

    // Retry connect with exponential backoff; optionally wait for onOpen.
    private fun connectWithRetry(
        endpoint: SharedEndpoint,
        uri: URI,
        maxAttempts: Int = 6,
        maxBackoffMs: Long = 2000,
    ): Boolean {
        var attempt = 0
        while (attempt < maxAttempts) {
            attempt++
            if (replaceClient(endpoint, uri)) {
                val ok = runCatching {
                    runBlocking {
                        withTimeoutOrNull(500) { endpoint.openSignal?.await() } != null && (endpoint.client?.isOpen == true)
                    }
                }.getOrDefault(false)
                if (ok) return true
            }
            val delayMs = min(150L * (1 shl (attempt - 1)), maxBackoffMs)
            try { Thread.sleep(delayMs) } catch (_: InterruptedException) {}
        }
        return false
    }

    // Connect to existing server first; if unavailable, optionally auto-host on port.
    private fun connectOrStartServerBlocking(endpoint: SharedEndpoint): Boolean {
        val now = System.currentTimeMillis()
        if (now < endpoint.nextRetryAt) return false

        val targetUri = endpoint.key.uri
        synchronized(endpoint.connectLock) {
            endpoint.client?.let {
                if (it.isOpen) {
                    endpoint.nextRetryAt = 0
                    return true
                }
            }

            if (connectWithRetry(endpoint, targetUri)) {
                endpoint.nextRetryAt = 0
                return true
            }

            if (!endpoint.key.autoHostOnPort) {
                endpoint.nextRetryAt = System.currentTimeMillis() + RETRY_COOLDOWN_MS
                return false
            }

            if (!endpoint.autoHostAnnounced) {
                endpoint.autoHostAnnounced = true
                endpoint.logger.info(
                    "No active WebSocket server on ${targetUri.host}:${targetUri.port}. " +
                        "Attempting to auto-host on ${endpoint.key.hostWhenAutoHosting}:${targetUri.port}."
                )
            }

            startServer(endpoint, endpoint.key.hostWhenAutoHosting, targetUri.port)
            try { Thread.sleep(200) } catch (_: InterruptedException) {}
            val localFallback = URI("${targetUri.scheme}://127.0.0.1:${targetUri.port}${targetUri.rawPath ?: ""}")
            val connected = connectWithRetry(endpoint, localFallback)
            endpoint.nextRetryAt = if (connected) 0 else System.currentTimeMillis() + RETRY_COOLDOWN_MS
            return connected
        }
    }

    private fun handleMessage(message: String) {
        val result = message.asPayloadWrapper<R>()
        when (result.behaviour) {
            PayloadBehaviour.FORWARD -> {
                val wrappedPayload = message.asPayloadWrapper<P>()
                if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return
                val payloadResult = handle(wrappedPayload.typedPayload(messageMeta.payloadType)) ?: return

                endpoint.client?.takeIf { it.isOpen }?.send(
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
                            complete(result.typedPayload(messageMeta.resultType))
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

        launch(this@WebSocketPublisher.connectionMeta.dispatcher) {
            val connected = connectOrStartServerBlocking(endpoint)
            val ws = endpoint.client
            if (!connected || ws == null || !ws.isOpen) {
                result.completeExceptionally(
                    IllegalStateException("WebSocket is not connected to ${connectionMeta.uri}. Payload not sent.")
                )
                payloads.remove(payload.uniqueId)
                return@launch
            }

            try {
                withTimeoutOrNull(500) { endpoint.openSignal?.await() }
                ws.send(payload.asJson())
            } catch (e: Exception) {
                result.completeExceptionally(e)
                payloads.remove(payload.uniqueId)
            }
        }

        return result
    }

    private fun closeQuietly() {
        if (closed) return
        closed = true
        releaseEndpoint()
    }

    open fun shutdown() {
        closeQuietly()
        unregister()
    }
}
