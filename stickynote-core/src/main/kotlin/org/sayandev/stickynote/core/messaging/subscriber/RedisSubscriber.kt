package org.sayandev.stickynote.core.messaging.subscriber

import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper;
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson;
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper;
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload;
import org.sayandev.stickynote.core.messaging.publisher.Publisher;
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import java.util.*;
import java.util.concurrent.*;
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
    val subJedis = redis.resource
    val pubJedis = redis.resource
    private val executor = Executors.newSingleThreadExecutor(ThreadFactoryBuilder().setNameFormat("redis-sub-pub-thread-${channel}-%d").build())
    private val TIMEOUT_SECONDS = 5L

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
                    }

                    PayloadWrapper.State.FORWARD -> {
                        if (payloadWrapper.excludeSource && isSource(payloadWrapper.uniqueId)) return
                        launch(dispatcher) {
                            val result =
                                (HANDLER_LIST.find { it.namespace == this@RedisSubscriber.namespace && it.name == this@RedisSubscriber.name } as? Subscriber<P, S>)?.onSubscribe(
                                    payloadWrapper.typedPayload(payloadClass)
                                )
                            if (payloadWrapper.target == "PROCESSED") return@launch;
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
        };
        Thread({ subJedis.subscribe(pubSub, channel) }, "redis-sub-sub-thread-${channel}-${UUID.randomUUID().toString().split("-").first()}").start()
    }

    private fun publishWithTimeout(payload: PayloadWrapper<*>) {
        val future = executor.submit<Boolean> {
            pubJedis.publish(channel.toByteArray(), payload.asJson().toByteArray());
            return@submit true;
        }

        try {
            if (!future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                logger.warning("failed to publish payload `${payload}` within $TIMEOUT_SECONDS seconds.")
            }
        } catch (e: TimeoutException) {
            logger.warning("failed to publish payload `${payload}` after $TIMEOUT_SECONDS seconds. (timed-out)")
            future.cancel(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isSource(uniqueId: UUID): Boolean {
        return Publisher.HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.contains(uniqueId);
    }
}
