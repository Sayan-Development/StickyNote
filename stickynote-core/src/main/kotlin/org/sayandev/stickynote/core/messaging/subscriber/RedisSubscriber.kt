package org.sayandev.stickynote.core.messaging.subscriber

import kotlinx.coroutines.CoroutineDispatcher
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub
import java.util.*

abstract class RedisSubscriber<P, S>(
    val dispatcher: CoroutineDispatcher,
    val publisherRedis: Jedis,
    val redis: Jedis,
    namespace: String,
    name: String,
    val payloadClass: Class<P>
) : Subscriber<P, S>(namespace, name) {

    val channel = "$namespace:$name"

    init {
        val pubSub = object : JedisPubSub() {
            override fun onMessage(channel: String, message: String) {
                if (channel != this@RedisSubscriber.channel) return

                val payloadWrapper = message.asPayloadWrapper<P>()

                when (payloadWrapper.state) {
                    PayloadWrapper.State.PROXY -> {
                        val isVelocity = runCatching { Class.forName("com.velocitypowered.api.proxy.ProxyServer") != null }.isSuccess
                        if (!isVelocity) return

                        launch(dispatcher) {
                            val result =
                                (HANDLER_LIST.find { it.namespace == this@RedisSubscriber.namespace && it.name == this@RedisSubscriber.name } as Subscriber<P, S>).onSubscribe(
                                    payloadWrapper.typedPayload(payloadClass)
                                )
                            result.invokeOnCompletion {
                                Thread {
                                    redis.publish(
                                        channel.toByteArray(),
                                        PayloadWrapper(
                                            payloadWrapper.uniqueId,
                                            result.getCompleted(),
                                            PayloadWrapper.State.RESPOND,
                                            payloadWrapper.source
                                        ).asJson().toByteArray()
                                    )
                                }.start()
                            }
                        }
                    }
                    PayloadWrapper.State.FORWARD -> {
                        if (payloadWrapper.excludeSource && isSource(payloadWrapper.uniqueId)) return
                        launch(dispatcher) {
                            val result =
                                (HANDLER_LIST.find { it.namespace == this@RedisSubscriber.namespace && it.name == this@RedisSubscriber.name } as? Subscriber<P, S>)?.onSubscribe(
                                    payloadWrapper.typedPayload(payloadClass)
                                )
                            if (payloadWrapper.target == "PROCESSED") return@launch
                            result?.invokeOnCompletion {
                                publisherRedis.publish(
                                    channel.toByteArray(),
                                    PayloadWrapper(
                                        payloadWrapper.uniqueId,
                                        result.getCompleted(),
                                        PayloadWrapper.State.RESPOND,
                                        source = payloadWrapper.source,
                                    ).asJson().toByteArray()
                                )
                            } ?: let {
                                publisherRedis.publish(
                                    channel.toByteArray(),
                                    PayloadWrapper(
                                        payloadWrapper.uniqueId,
                                        payloadWrapper.payload,
                                        payloadWrapper.state,
                                        payloadWrapper.source,
                                        "PROCESSED"
                                    ).asJson().toByteArray()
                                )
                            }
                        }
                    }
                    PayloadWrapper.State.RESPOND -> {}
                }
            }
        }
        Thread {
            redis.subscribe(pubSub, channel)
        }.start()
    }

    fun isSource(uniqueId: UUID): Boolean {
        return Publisher.HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.contains(uniqueId)
    }

}