package org.sayandev.stickynote.core.messaging.publisher

import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.CompletableDeferred;
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson;
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper;
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import java.util.*
import java.util.concurrent.*;
import java.util.logging.Logger;

abstract class RedisPublisher<P, S>(
    val redis: JedisPool,
    namespace: String,
    name: String,
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
    private val executor = Executors.newSingleThreadExecutor(ThreadFactoryBuilder().setNameFormat("redis-pub-pub-thread-${channel}-%d").build())
    private val TIMEOUT_SECONDS = 5L

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
        };
        Thread({ subJedis.subscribe(pubSub, channel) }, "redis-pub-sub-thread-${channel}-${UUID.randomUUID().toString().split("-").first()}").start()
    }

    override fun publish(payloadWrapper: PayloadWrapper<P>): CompletableDeferred<S> {
        val future = executor.submit<Boolean> {
            pubJedis.publish(channel.toByteArray(), payloadWrapper.asJson().toByteArray());
            return@submit true;
        }

        try {
            if (!future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                logger.warning("failed to publish payload `${payloadWrapper}` within $TIMEOUT_SECONDS seconds.")
            }
        } catch (e: TimeoutException) {
            logger.warning("failed to publish payload `${payloadWrapper}` after $TIMEOUT_SECONDS seconds. (timed-out)")
            future.cancel(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return super.publish(payloadWrapper)
    }

    abstract fun handle(payload: P): S?
}
