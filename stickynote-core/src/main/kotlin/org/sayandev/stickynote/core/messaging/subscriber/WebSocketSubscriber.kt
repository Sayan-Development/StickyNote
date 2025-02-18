package org.sayandev.stickynote.core.messaging.subscriber

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import org.sayandev.stickynote.core.utils.CoroutineUtils.awaitWithTimeout
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import java.net.URI
import java.util.*
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
abstract class WebSocketSubscriber<P, S>(
    val dispatcher: CoroutineContext,
    serverUri: URI,
    namespace: String,
    name: String,
    val payloadClass: Class<P>,
    val logger: Logger
) : Subscriber<P, S>(namespace, name) {

    val channel = "$namespace:$name"
    private val client: WebSocketClient

    init {
        client = object : WebSocketClient(serverUri) {
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
        val payloadWrapper = message.asPayloadWrapper<P>()

        when (payloadWrapper.state) {
            PayloadWrapper.State.PROXY -> {
                val isVelocity = runCatching { Class.forName("com.velocitypowered.api.proxy.ProxyServer") }.isSuccess
                if (!isVelocity) return

                launch(dispatcher) {
                    val result = (HANDLER_LIST.find { it.namespace == this@WebSocketSubscriber.namespace && it.name == this@WebSocketSubscriber.name } as Subscriber<P, S>)
                        .onSubscribe(payloadWrapper.typedPayload(payloadClass))
                    result.await()
                    publishWithTimeout(
                        PayloadWrapper(
                            payloadWrapper.uniqueId,
                            result.getCompleted(),
                            PayloadWrapper.State.RESPOND,
                            payloadWrapper.source
                        )
                    )
                }
            }

            PayloadWrapper.State.FORWARD -> {
                if (payloadWrapper.excludeSource && isSource(payloadWrapper.uniqueId)) return
                launch(dispatcher) {
                    val result =
                        (HANDLER_LIST.find { it.namespace == this@WebSocketSubscriber.namespace && it.name == this@WebSocketSubscriber.name } as? Subscriber<P, S>)?.onSubscribe(
                            payloadWrapper.typedPayload(payloadClass)
                        )
                    if (payloadWrapper.target == "PROCESSED") return@launch
                    publishWithTimeout(
                        PayloadWrapper(
                            payloadWrapper.uniqueId,
                            result?.getCompleted() ?: payloadWrapper.payload,
                            if (result != null) PayloadWrapper.State.RESPOND else payloadWrapper.state,
                            payloadWrapper.source,
                            "PROCESSED"
                        )
                    )
                }
            }

            PayloadWrapper.State.RESPOND -> {}
        }
    }

    private suspend fun publishWithTimeout(payload: PayloadWrapper<*>) {
        val deferred = CompletableDeferred<Unit>()
        launch(dispatcher) {
            client.send(payload.asJson())
            deferred.complete(Unit)
        }
        deferred.awaitWithTimeout(TIMEOUT_SECONDS * 1000L) {
            logger.warning("failed to publish payload `${payload}` within $TIMEOUT_SECONDS seconds.")
        }
    }

    fun isSource(uniqueId: UUID): Boolean {
        return Publisher.HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.contains(uniqueId)
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L
    }
}