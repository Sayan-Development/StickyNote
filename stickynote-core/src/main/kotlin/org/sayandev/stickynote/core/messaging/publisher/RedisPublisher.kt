package org.sayandev.stickynote.core.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import org.sayandev.stickynote.core.coroutine.dispatcher.AsyncDispatcher
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.subscriber.Subscriber
import org.sayandev.stickynote.core.messaging.subscriber.Subscriber.Companion
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import redis.clients.jedis.Jedis
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
) : Publisher<P, S>(logger, namespace, name), AutoCloseable {

    val channel = "$namespace:$name"
    private var subJedis: Jedis? = null
    private var pubSub: JedisPubSub? = null
    private var subscriptionThread: Thread? = null
    private var isRunning = true

    init {
        connect()
    }

    private fun connect() {
        try {
            // Close previous resources if they exist
            subJedis?.close()
            pubSub?.unsubscribe()
            subscriptionThread?.interrupt()

            subJedis = redis.resource
            pubSub = createPubSub()

            subscriptionThread = Thread({
                while (!Thread.interrupted() && isRunning && !redis.isClosed && subJedis?.isBroken == false) {
                    try {
                        subJedis?.subscribe(pubSub, channel)
                    } catch (e: Exception) {
                        logger.warning("Redis subscription error: ${e.message}")
                        Thread.sleep(1000)
                        reconnect()
                    }
                }

                // Add reconnection if the while loop condition fails
                if (isRunning && !redis.isClosed) {
                    logger.warning("Redis connection lost, attempting to reconnect...")
                    Thread.sleep(1000)
                    reconnect()
                }
            }, "redis-sub-thread-${channel}-${UUID.randomUUID().toString().split("-").first()}")
            subscriptionThread?.start()
        } catch (e: Exception) {
            logger.severe("Failed to establish Redis connection: ${e.message}")
        }
    }

    private fun reconnect() {
        if (!isRunning) return
        try {
            subJedis?.close()
            connect()
        } catch (e: Exception) {
            logger.severe("Failed to reconnect: ${e.message}")
        }
    }

    private fun createPubSub() = object : JedisPubSub() {
        override fun onMessage(channel: String, message: String) {
            if (channel != this@RedisPublisher.channel) return
            val result = message.asPayloadWrapper<S>()

            when (result.state) {
                PayloadWrapper.State.FORWARD -> handleForwardMessage(message)
                PayloadWrapper.State.RESPOND -> handleResponseMessage(result)
                PayloadWrapper.State.PROXY -> {} // Handle proxy if needed
            }
        }
    }

    private fun handleForwardMessage(message: String) {
        val wrappedPayload = message.asPayloadWrapper<P>()
        if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return

        val payloadResult = handle(wrappedPayload.typedPayload(payloadClass)) ?: return
        redis.resource.use { jedis ->
            jedis.publish(
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
    }

    private fun handleResponseMessage(result: PayloadWrapper<S>) {
        for (publisher in HANDLER_LIST.filterIsInstance<RedisPublisher<P, S>>()) {
            if (publisher.id() == channel) {
                publisher.payloads[result.uniqueId]?.apply {
                    complete(result.typedPayload(resultClass))
                    publisher.payloads.remove(result.uniqueId)
                }
            }
        }
    }

    override suspend fun publish(payload: PayloadWrapper<P>): CompletableDeferred<S> {
        val result = super.publish(payload)

        launch(dispatcher) {
            delay(TIMEOUT_SECONDS * 1000L)
            if (result.isActive) {
                result.completeExceptionally(IllegalStateException(
                    "Sent payload has not been responded in $TIMEOUT_SECONDS seconds. Payload: $payload (channel: ${id()}"
                ))
                payloads.remove(payload.uniqueId)
            }
        }

        redis.resource.use { jedis ->
            jedis.publish(channel.toByteArray(), payload.asJson().toByteArray())
        }

        return result
    }

    abstract fun handle(payload: P): S?

    fun isSource(uniqueId: UUID): Boolean =
        HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.contains(uniqueId)

    override fun close() {
        isRunning = false
        pubSub?.unsubscribe()
        subJedis?.close()
        subscriptionThread?.interrupt()
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L
    }
}