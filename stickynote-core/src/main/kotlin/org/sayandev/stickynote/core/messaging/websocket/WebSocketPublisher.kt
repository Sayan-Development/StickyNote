package org.sayandev.stickynote.core.messaging.websocket

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import org.java_websocket.client.WebSocketClient
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
import java.util.logging.Logger

open class  WebSocketPublisher<P : Any, R : Any>(
    messageMeta: MessageMeta<P, R>,
    connectionMeta: WebSocketConnectionMeta,
    logger: Logger,
) : Publisher<WebSocketConnectionMeta, P, R>(
    messageMeta,
    connectionMeta,
    logger,
) {
    private val client: WebSocketClient
    private val server: WebSocketServer

    init {
        server = MessageWebSocketServer.getWebSocketServer(connectionMeta.uri) ?: MessageWebSocketServer(connectionMeta.uri).apply {
            logger.info("Starting a new WebSocket server at ${connectionMeta.uri}")
            start()
        }

        client = object : WebSocketClient(connectionMeta.uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
            }

            override fun onMessage(message: String) {
                handleMessage(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                logger.info("WebSocket connection closed: $reason")
            }

            override fun onError(ex: Exception?) {
                logger.severe("WebSocket error: ${ex?.message}")
            }
        }
        client.connect()
    }

    private fun handleMessage(message: String) {
        val result = message.asPayloadWrapper<R>()
        when (result.behaviour) {
            PayloadBehaviour.FORWARD -> {
                val wrappedPayload = message.asPayloadWrapper<P>()
                if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return
                val payloadResult = handle(wrappedPayload.typedPayload(messageMeta.payloadType)) ?: return
                client.send(
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

        launch(this@WebSocketPublisher.connectionMeta.dispatcher) {
            delay(connectionMeta.timeoutMillis)
            if (result.isActive) {
                result.completeExceptionally(IllegalStateException("Sent payload has not been responded in ${connectionMeta.timeoutMillis}ms. Payload: $payload (id: ${messageMeta.id()})"))
            }
            payloads.remove(payload.uniqueId)
        }

        launch(this@WebSocketPublisher.connectionMeta.dispatcher) {
            client.send(payload.asJson())
        }

        return result
    }
}