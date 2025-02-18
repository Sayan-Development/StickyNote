package org.sayandev.stickynote.core.messaging.publisher.websocket

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import java.util.logging.Logger

open class  WebSocketPublisher<P : Any, S : Any>(
    messageMeta: MessageMeta<P, S>,
    publisherMeta: WebSocketPublisherMeta,
    logger: Logger,
) : Publisher<WebSocketPublisherMeta, P, S>(
    messageMeta,
    publisherMeta,
    logger,
) {
    private val client: WebSocketClient

    init {
        client = object : WebSocketClient(publisherMeta.uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                logger.info("WebSocket connection opened")
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
        val result = message.asPayloadWrapper<S>()
        when (result.state) {
            PayloadWrapper.State.FORWARD -> {
                val wrappedPayload = message.asPayloadWrapper<P>()
                if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return
                val payloadResult = handle(wrappedPayload.typedPayload(messageMeta.payloadType)) ?: return
                client.send(
                    PayloadWrapper(
                        wrappedPayload.uniqueId,
                        payloadResult,
                        PayloadWrapper.State.RESPOND,
                        wrappedPayload.source,
                        wrappedPayload.target,
                        wrappedPayload.excludeSource
                    ).asJson()
                )
            }
            PayloadWrapper.State.RESPOND -> {
                for (publisher in HANDLER_LIST.filterIsInstance<WebSocketPublisher<P, S>>()) {
                    if (publisher.messageMeta.id() == messageMeta.id()) {
                        publisher.payloads[result.uniqueId]?.apply {
                            this.complete(result.typedPayload(messageMeta.resultType))
                            publisher.payloads.remove(result.uniqueId)
                        }
                    }
                }
            }
            PayloadWrapper.State.PROXY -> {}
        }
    }

    override suspend fun publish(payload: PayloadWrapper<P>): CompletableDeferred<S> {
        if (!HANDLER_LIST.contains(this)) {
            throw IllegalStateException("Publisher with id ${messageMeta.id()} is not registered")
        }

        val result = super.publish(payload)

        launch(publisherMeta.dispatcher) {
            delay(TIMEOUT_SECONDS * 1000L)
            if (result.isActive) {
                result.completeExceptionally(IllegalStateException("Sent payload has not been responded in $TIMEOUT_SECONDS seconds. Payload: $payload (id: ${messageMeta.id()})"))
            }
            payloads.remove(payload.uniqueId)
        }

        launch(publisherMeta.dispatcher) {
            client.send(payload.asJson())
        }

        return result
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L

        inline fun <reified P : Any, reified S : Any> create(messageMeta: MessageMeta<P, S>, publisherMeta: WebSocketPublisherMeta, logger: Logger, crossinline respondHandle: (payload: P) -> S? = { null }): WebSocketPublisher<P, S> {
            return object : WebSocketPublisher<P, S>(messageMeta, publisherMeta, logger) {
                override fun handle(payload: P): S? {
                    return respondHandle(payload)
                }
            }
        }
    }
}