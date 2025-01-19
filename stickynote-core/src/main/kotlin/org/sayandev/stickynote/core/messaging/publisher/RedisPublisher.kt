package org.sayandev.stickynote.core.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.JedisPubSub
import java.util.logging.Logger

abstract class RedisPublisher<P, S>(
    val redis: JedisPool,
    namespace: String,
    name: String,
    val resultClass: Class<S>,
    logger: Logger,
) : Publisher<P, S>(
    logger,
    namespace,
    name
) {
    val channel = "$namespace:$name"

    init {
        val pubSub = object : JedisPubSub() {
            override fun onMessage(channel: String, message: String) {
                if (channel != this@RedisPublisher.channel) return
                val result = message.asPayloadWrapper<S>()
                when (result.state) {
                    PayloadWrapper.State.RESPOND -> {
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
                    else -> {}
                }
            }
        }
        Thread {
            redis.resource.subscribe(pubSub, channel)
        }.start()
    }

    override fun publish(payloadWrapper: PayloadWrapper<P>): CompletableDeferred<S> {
        Thread {
            redis.resource.publish(channel.toByteArray(), payloadWrapper.asJson().toByteArray())
        }.start()
        return super.publish(payloadWrapper)
    }

    abstract fun handle(payload: P): S?
}