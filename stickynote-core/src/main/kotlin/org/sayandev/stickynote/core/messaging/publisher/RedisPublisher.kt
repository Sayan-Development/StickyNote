package org.sayandev.stickynote.core.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.subscriber.RedisSubscriber
import org.sayandev.stickynote.core.utils.CoroutineUtils.awaitWithTimeout
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import java.util.*
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

    private val subJedis = redis.resource
    private val pubJedis = redis.resource

    init {
        val pubSub = object : JedisPubSub() {
            override fun onMessage(channel: String, message: String) {
                if (channel != this@RedisPublisher.channel) return
                val result = message.asPayloadWrapper<S>()
                when (result.state) {
                    PayloadWrapper.State.FORWARD -> {
                        val wrappedPayload = message.asPayloadWrapper<P>()
                        println("Before ${HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.joinToString(", ")}")
                        println("UniqueId: ${wrappedPayload.uniqueId}")
                        println("Exclude: ${wrappedPayload.excludeSource} isSource: ${isSource(wrappedPayload.uniqueId)}")
                        if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return
                        println("After")
                        val payloadResult = handle(wrappedPayload.typedPayload(payloadClass)) ?: return
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
                        println("Respond")
                        for (publisher in HANDLER_LIST.filterIsInstance<RedisPublisher<P, S>>()) {
                            if (publisher.id() == channel) {
                                publisher.payloads[result.uniqueId]?.apply {
                                    this.complete(result.typedPayload(resultClass))
                                    publisher.payloads.remove(result.uniqueId)
                                }
                            }
                        }
                    }
                    PayloadWrapper.State.PROXY -> {}
                }
            }
        }
        Thread({ subJedis.subscribe(pubSub, channel) }, "redis-pub-sub-thread-${channel}-${UUID.randomUUID().toString().split("-").first()}").start()
    }

    override suspend fun publish(payload: PayloadWrapper<P>): CompletableDeferred<S> {
        val result = super.publish(payload)

        launch(dispatcher) {
            pubJedis.publish(channel.toByteArray(), payload.asJson().toByteArray())
        }

        return result
    }

    abstract fun handle(payload: P): S?

    fun isSource(uniqueId: UUID): Boolean {
        return HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.contains(uniqueId)
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L
    }
}
