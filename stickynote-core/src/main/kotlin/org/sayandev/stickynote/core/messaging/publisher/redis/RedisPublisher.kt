package org.sayandev.stickynote.core.messaging.publisher.redis

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import java.util.*
import java.util.logging.Logger

open class RedisPublisher<P : Any, S : Any>(
    meta: MessageMeta<P, S>,
    val dispatcher: CoroutineDispatcher,
    val redis: JedisPool,
    logger: Logger
) : Publisher<P, S>(
    meta,
    logger,
) {

    private val subJedis = redis.resource
    private val pubJedis = redis.resource

    init {
        val pubSub = object : JedisPubSub() {
            override fun onMessage(channel: String, message: String) {
                if (channel != this@RedisPublisher.meta.id()) return
                val result = message.asPayloadWrapper<S>()
                when (result.state) {
                    PayloadWrapper.State.FORWARD -> {
                        val wrappedPayload = message.asPayloadWrapper<P>()
                        if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return
                        val payloadResult = handle(wrappedPayload.typedPayload(meta.payloadType)) ?: return
                        pubJedis.publish(
                            channel.toByteArray(),
                            PayloadWrapper(
                                wrappedPayload.uniqueId,
                                payloadResult,
                                PayloadWrapper.State.RESPOND,
                                wrappedPayload.source,
                                wrappedPayload.target,
                                wrappedPayload.excludeSource
                            ).asJson().toByteArray()
                        )
                    }
                    PayloadWrapper.State.RESPOND -> {
                        for (publisher in HANDLER_LIST.filterIsInstance<RedisPublisher<P, S>>()) {
                            if (publisher.meta.id() == channel) {
                                publisher.payloads[result.uniqueId]?.apply {
                                    this.complete(result.typedPayload(meta.resultType))
                                    publisher.payloads.remove(result.uniqueId)
                                }
                            }
                        }
                    }
                    PayloadWrapper.State.PROXY -> {}
                }
            }
        }
        Thread({ subJedis.subscribe(pubSub, meta.id()) }, "redis-pub-sub-thread-${meta.id()}-${UUID.randomUUID().toString().split("-").first()}").start()
    }

    override suspend fun publish(payload: PayloadWrapper<P>): CompletableDeferred<S> {
        if (!HANDLER_LIST.contains(this)) {
            throw IllegalStateException("Publisher with id ${meta.id()} is not registered")
        }

        val result = super.publish(payload)

        launch(dispatcher) {
            delay(TIMEOUT_SECONDS * 1000L)
            if (result.isActive) {
                result.completeExceptionally(IllegalStateException("Sent payload has not been responded in $TIMEOUT_SECONDS seconds. Payload: $payload (id: ${meta.id()})"))
            }
            payloads.remove(payload.uniqueId)
        }

        launch(dispatcher) {
            pubJedis.publish(meta.id().toByteArray(), payload.asJson().toByteArray())
        }

        return result
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L

        inline fun <reified P : Any, reified S : Any> create(meta: MessageMeta<P, S>, dispatcher: CoroutineDispatcher, redis: JedisPool, logger: Logger, crossinline respondHandle: (payload: P) -> S? = { null }): RedisPublisher<P, S> {
            return object : RedisPublisher<P, S>(meta, dispatcher, redis, logger) {
                override fun handle(payload: P): S? {
                    return respondHandle(payload)
                }
            }
        }
    }
}
