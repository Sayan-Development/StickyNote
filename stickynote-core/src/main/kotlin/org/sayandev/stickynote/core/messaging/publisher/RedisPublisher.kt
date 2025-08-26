package org.sayandev.stickynote.core.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import org.sayandev.stickynote.core.coroutine.dispatcher.AsyncDispatcher
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.redis.RedisConnectionManager
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import redis.clients.jedis.JedisPool
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

abstract class RedisPublisher<P, S>(
    val dispatcher: CoroutineDispatcher,
    val redis: JedisPool,
    namespace: String,
    name: String,
    val payloadClass: Class<P>,
    val resultClass: Class<S>,
    logger: Logger
) : Publisher<P, S>(
    logger,
    namespace,
    name
) {
    val channel = "$namespace:$name"

    init {
        RedisConnectionManager.registerNamespace(namespace, redis, dispatcher)
    }

    fun handleForwardMessage(payloadWrapper: PayloadWrapper<Any>) {
        if (payloadWrapper.excludeSource && isSource(payloadWrapper.uniqueId)) return

        try {
            val typedPayload = payloadWrapper.typedPayload(payloadClass)
            val payloadResult = handle(typedPayload) ?: return

            val localJedis = redis.resource
            try {
                localJedis.publish(
                    channel.toByteArray(),
                    PayloadWrapper(
                        payloadWrapper.uniqueId,

                        payloadResult,
                        PayloadWrapper.State.RESPOND,
                        payloadWrapper.source,
                        payloadWrapper.target,
                        payloadWrapper.excludeSource
                    ).asJson().toByteArray()
                )
            } finally {
                localJedis.close()
            }
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error handling forward message: ${e.message}")
        }
    }

    fun handleResponseMessage(payloadWrapper: PayloadWrapper<Any>) {
        try {
            val typedResult = payloadWrapper.typedPayload(resultClass)
            payloads[payloadWrapper.uniqueId]?.apply {
                this.complete(typedResult)
                payloads.remove(payloadWrapper.uniqueId)
            }
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error handling response message: ${e.message}")
        }
    }

    override suspend fun publish(payloadWrapper: PayloadWrapper<P>): CompletableDeferred<S> {
        val result = super.publish(payloadWrapper)

        launch(dispatcher) {
            val localJedis = redis.resource
            try {
                val published = localJedis.publish(channel.toByteArray(), payloadWrapper.asJson().toByteArray())
                if (published <= 0) {
                    payloads.remove(payloadWrapper.uniqueId)
                    return@launch
                }
            } finally {
                localJedis.close()
            }

            delay(TIMEOUT_SECONDS * 1000L)
            if (result.isActive) {
                result.completeExceptionally(IllegalStateException("No response received in $TIMEOUT_SECONDS seconds"))
                payloads.remove(payloadWrapper.uniqueId)
            }
        }

        return result
    }

    abstract fun handle(payload: P): S?

    fun isSource(uniqueId: UUID): Boolean {
        return HANDLER_LIST.asSequence()
            .flatMap { publisher -> publisher.payloads.keys.asSequence() }.contains(uniqueId)
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L
    }
}