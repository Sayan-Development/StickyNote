package org.sayandev.stickynote.core.messaging.subscriber

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import org.sayandev.stickynote.core.messaging.redis.RedisConnectionManager
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import redis.clients.jedis.JedisPool
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@OptIn(ExperimentalCoroutinesApi::class)
abstract class RedisSubscriber<P, S>(
    val dispatcher: CoroutineDispatcher,
    val redis: JedisPool,
    namespace: String,
    name: String,
    val payloadClass: Class<P>,
    val logger: Logger
) : Subscriber<P, S>(namespace, name) {

    val channel = "$namespace:$name"

    init {
        RedisConnectionManager.registerNamespace(namespace, redis, dispatcher)
    }

    fun handleForwardMessage(payloadWrapper: PayloadWrapper<Any>) {
        if (payloadWrapper.excludeSource && isSource(payloadWrapper.uniqueId)) return
        launch(dispatcher) {
            try {
                val typedPayload = payloadWrapper.typedPayload(payloadClass)
                val result = onSubscribe(typedPayload)
                if (result == null) return@launch
                if (payloadWrapper.target == "PROCESSED") return@launch

                publish(
                    PayloadWrapper(
                        payloadWrapper.uniqueId,
                        result.getCompleted(),
                        PayloadWrapper.State.RESPOND,
                        payloadWrapper.source,
                        "PROCESSED"
                    )
                )
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Error handling forward message: ${e.message}")
            }
        }
    }

    fun handleProxyMessage(payloadWrapper: PayloadWrapper<Any>) {
        if (!isVelocity) return

        launch(dispatcher) {
            try {
                val typedPayload = payloadWrapper.typedPayload(payloadClass)
                val result = onSubscribe(typedPayload) ?: return@launch
                result.await()
                publish(
                    PayloadWrapper(
                        payloadWrapper.uniqueId,
                        result.getCompleted(),
                        PayloadWrapper.State.RESPOND,
                        payloadWrapper.source
                    )
                )
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Error handling proxy message: ${e.message}")
            }
        }
    }

    private suspend fun publish(payload: PayloadWrapper<*>) {
        val publication = CompletableDeferred<Unit>()
        launch(dispatcher) {
            delay(TIMEOUT_SECONDS * 1000)
            if (publication.isActive) {
                publication.completeExceptionally(IllegalStateException("Failed to publish payload in subscriber after $TIMEOUT_SECONDS seconds. Payload: $payload (channel: ${id()})"))
            }
        }

        val localJedis = redis.resource
        try {
            val published = localJedis.publish(channel.toByteArray(), payload.asJson().toByteArray())
            if (published <= 0) {
                return
            }
            publication.complete(Unit)
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error publishing message: ${e.message}")
            publication.completeExceptionally(e)
        } finally {
            localJedis.close()
        }
    }

    fun isSource(uniqueId: UUID): Boolean {
        return Publisher.HANDLER_LIST
            .asSequence()
            .flatMap { publisher -> publisher.payloads.keys.asSequence() }
            .contains(uniqueId)
    }

    companion object {
        val isVelocity = runCatching { Class.forName("com.velocitypowered.api.proxy.ProxyServer") != null }.isSuccess

        const val TIMEOUT_SECONDS = 5L
    }
}