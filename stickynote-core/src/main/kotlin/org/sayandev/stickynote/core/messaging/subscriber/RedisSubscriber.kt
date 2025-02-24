package org.sayandev.stickynote.core.messaging.subscriber

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import java.util.*
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
abstract class RedisSubscriber<P, S>(
    val dispatcher: CoroutineContext,
    val redis: JedisPool,
    namespace: String,
    name: String,
    val payloadClass: Class<P>,
    val logger: Logger
) : Subscriber<P, S>(namespace, name), AutoCloseable {

    val isVelocity = runCatching { Class.forName("com.velocitypowered.api.proxy.ProxyServer") != null }.isSuccess

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
            if (channel != this@RedisSubscriber.channel) return
            val payloadWrapper = message.asPayloadWrapper<P>()

            when (payloadWrapper.state) {
                PayloadWrapper.State.PROXY -> handleProxyMessage(payloadWrapper)
                PayloadWrapper.State.FORWARD -> handleForwardMessage(payloadWrapper)
                PayloadWrapper.State.RESPOND -> {} // Handle response if needed
            }
        }
    }

    private fun handleProxyMessage(payloadWrapper: PayloadWrapper<P>) {
        if (!isVelocity) return

        launch(dispatcher) {
            val result = (HANDLER_LIST.find { it.namespace == namespace && it.name == name } as Subscriber<P, S>)
                .onSubscribe(payloadWrapper.typedPayload(payloadClass))
            result.await()
            publish(PayloadWrapper(
                payloadWrapper.uniqueId,
                result.getCompleted(),
                PayloadWrapper.State.RESPOND,
                payloadWrapper.source
            ))
        }
    }

    private fun handleForwardMessage(payloadWrapper: PayloadWrapper<P>) {
        if (payloadWrapper.excludeSource && isSource(payloadWrapper.uniqueId)) return
        launch(dispatcher) {
            val result = (HANDLER_LIST.find { it.namespace == namespace && it.name == name } as? Subscriber<P, S>)
                ?.onSubscribe(payloadWrapper.typedPayload(payloadClass))
            if (payloadWrapper.target == "PROCESSED") return@launch
            publish(PayloadWrapper(
                payloadWrapper.uniqueId,
                result?.getCompleted() ?: payloadWrapper.payload,
                if (result != null) PayloadWrapper.State.RESPOND else payloadWrapper.state,
                payloadWrapper.source,
                "PROCESSED"
            ))
        }
    }

    private suspend fun publish(payload: PayloadWrapper<*>) {
        val publication = CompletableDeferred<Unit>()
        launch(dispatcher) {
            delay(TIMEOUT_SECONDS * 1000)
            if (publication.isActive) {
                publication.completeExceptionally(IllegalStateException(
                    "Failed to publish payload in subscriber after $TIMEOUT_SECONDS seconds. Payload: $payload (channel: ${id()})"
                ))
            }
        }

        redis.resource.use { jedis ->
            jedis.publish(channel.toByteArray(), payload.asJson().toByteArray())
        }
    }

    fun isSource(uniqueId: UUID): Boolean {
        return Publisher.HANDLER_LIST.flatMap { it.payloads.keys }.contains(uniqueId)
    }

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