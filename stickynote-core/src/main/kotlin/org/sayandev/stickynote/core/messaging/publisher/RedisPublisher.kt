package org.sayandev.stickynote.core.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import org.sayandev.stickynote.core.coroutine.dispatcher.AsyncDispatcher
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import redis.clients.jedis.exceptions.JedisException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
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
    private var subJedis = redis.resource
    private var subscriberThread: Thread? = null
    private val isSubscribed = AtomicBoolean(false)
    private val shouldReconnect = AtomicBoolean(true)
    private val pubSub = createPubSub()

    init {
        startSubscriber()
    }

    private fun createPubSub(): JedisPubSub {
        return object : JedisPubSub() {
            override fun onMessage(channel: String, message: String) {
                if (channel != this@RedisPublisher.channel) return
                try {
                    val result = message.asPayloadWrapper<S>()
                    when (result.state) {
                        PayloadWrapper.State.FORWARD -> handleForward(message)
                        PayloadWrapper.State.RESPOND -> handleResponse(result)
                        PayloadWrapper.State.PROXY -> {}
                    }
                } catch (e: Exception) {
                    logger.log(Level.WARNING, "Error processing message: ${e.message}")
                }
            }
        }
    }

    private fun handleForward(message: String) {
        val wrappedPayload = message.asPayloadWrapper<P>()
        if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return
        val payloadResult = handle(wrappedPayload.typedPayload(payloadClass)) ?: return

        val localJedis = redis.resource
        try {
            localJedis.publish(
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
        } finally {
            localJedis.close()
        }
    }

    private fun handleResponse(result: PayloadWrapper<S>) {
        for (publisher in HANDLER_LIST.filterIsInstance<RedisPublisher<P, S>>()) {
            if (publisher.id() == channel) {
                publisher.payloads[result.uniqueId]?.apply {
                    this.complete(result.typedPayload(resultClass))
                    publisher.payloads.remove(result.uniqueId)
                }
            }
        }
    }

    private fun startSubscriber() {
        if (!shouldReconnect.get() || isSubscribed.get()) return

        synchronized(this) {
            if (isSubscribed.get()) return

            subscriberThread?.interrupt()
            subscriberThread = Thread({
                while (shouldReconnect.get()) {
                    try {
                        subJedis = redis.resource
                        isSubscribed.set(true)
                        subJedis.subscribe(pubSub, channel)
                    } catch (e: JedisException) {
                        logger.log(Level.WARNING, "Redis connection lost: ${e.message}")
                        isSubscribed.set(false)
                        safeCloseJedis()
                        Thread.sleep(5000) // Wait before reconnecting
                    } catch (e: Exception) {
                        logger.log(Level.SEVERE, "Unexpected error in subscriber: ${e.message}")
                        isSubscribed.set(false)
                        safeCloseJedis()
                        Thread.sleep(5000)
                    }
                }
            }, "redis-pub-sub-thread-${channel}-${UUID.randomUUID().toString().split("-").first()}")
            subscriberThread?.start()
        }
    }

    private fun safeCloseJedis() {
        try {
            subJedis.close()
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error closing Jedis connection: ${e.message}")
        }
    }

    override suspend fun publish(payload: PayloadWrapper<P>): CompletableDeferred<S> {
        val result = super.publish(payload)

        launch(dispatcher) {
            delay(TIMEOUT_SECONDS * 1000L)
            if (result.isActive) {
                result.completeExceptionally(IllegalStateException("Sent payload has not been responded in $TIMEOUT_SECONDS seconds. Payload: $payload (channel: ${id()}"))
            }
            payloads.remove(payload.uniqueId)
        }

        val localJedis = redis.resource
        try {
            localJedis.publish(channel.toByteArray(), payload.asJson().toByteArray())
        } finally {
            localJedis.close()
        }

        return result
    }

    abstract fun handle(payload: P): S?

    fun isSource(uniqueId: UUID): Boolean {
        return HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.contains(uniqueId)
    }

    fun shutdown() {
        shouldReconnect.set(false)
        pubSub.unsubscribe()
        safeCloseJedis()
        subscriberThread?.interrupt()
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L

        init {
            launch(AsyncDispatcher("pub-debug-memory", 1)) {
                while (true) {
                    delay(30_000)
                    println("Current payload amount (pub): ${HANDLER_LIST.sumOf { it.payloads.size } }")
                }
            }
        }
    }
}