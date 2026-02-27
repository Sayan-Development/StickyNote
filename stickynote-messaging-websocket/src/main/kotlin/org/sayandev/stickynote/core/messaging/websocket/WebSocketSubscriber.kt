package org.sayandev.stickynote.core.messaging.websocket

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.PayloadBehaviour
import org.sayandev.stickynote.core.messaging.PayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.Publisher
import org.sayandev.stickynote.core.messaging.Subscriber
import org.sayandev.stickynote.core.utils.CoroutineUtils
import org.sayandev.stickynote.core.utils.CoroutineUtils.awaitWithTimeout
import java.util.UUID
import java.util.logging.Logger

@OptIn(ExperimentalCoroutinesApi::class)
abstract class WebSocketSubscriber<P : Any, R : Any>(
    messageMeta: MessageMeta<P, R>,
    val connectionMeta: WebSocketConnectionMeta,
    val logger: Logger
) : Subscriber<P, R>(messageMeta) {

    private val client: WebSocketClient

    init {
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
        val payloadWrapper = message.asPayloadWrapper<P>()

        when (payloadWrapper.behaviour) {
            PayloadBehaviour.FORWARD_PROXY -> {
                if (!isVelocity) return

                CoroutineUtils.launch(connectionMeta.dispatcher) {
                    val result = (HANDLER_LIST.find { it.messageMeta.id() == messageMeta.id() } as Subscriber<P, R>)
                        .onSubscribe(payloadWrapper.typedPayload(messageMeta.payloadType))
                    result.await()
                    publishWithTimeout(
                        PayloadWrapper(
                            payloadWrapper.uniqueId,
                            result.getCompleted(),
                            PayloadBehaviour.RESPONSE,
                            payloadWrapper.source
                        )
                    )
                }
            }
            PayloadBehaviour.FORWARD -> {
                if (payloadWrapper.excludeSource && isSource(payloadWrapper.uniqueId)) return
                CoroutineUtils.launch(connectionMeta.dispatcher) {
                    val result =
                        (HANDLER_LIST.find { it.messageMeta.id() == messageMeta.id() } as? Subscriber<P, R>)?.onSubscribe(
                            payloadWrapper.typedPayload(messageMeta.payloadType)
                        )
                    if (payloadWrapper.target == "PROCESSED") return@launch
                    publishWithTimeout(
                        PayloadWrapper(
                            payloadWrapper.uniqueId,
                            result?.getCompleted() ?: payloadWrapper.payload,
                            if (result != null) PayloadBehaviour.RESPONSE else payloadWrapper.behaviour,
                            payloadWrapper.source,
                            "PROCESSED"
                        )
                    )
                }
            }
            else -> { }
        }
    }

    private suspend fun publishWithTimeout(payload: PayloadWrapper<*>) {
        val deferred = CompletableDeferred<Unit>()
        CoroutineUtils.launch(connectionMeta.dispatcher) {
            client.send(payload.asJson())
            deferred.complete(Unit)
        }
        deferred.awaitWithTimeout(TIMEOUT_SECONDS * 1000L) {
            logger.warning("failed to publish payload `${payload}` within $TIMEOUT_SECONDS seconds.")
        }
    }

    fun isSource(uniqueId: UUID): Boolean {
        return Publisher.HANDLER_LIST.any { publisher -> publisher.payloads.containsKey(uniqueId) }
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L
    }
}
