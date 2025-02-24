package org.sayandev.stickynote.core.messaging.subscriber

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import org.sayandev.stickynote.core.messaging.publisher.RedisPublisher
import org.sayandev.stickynote.core.utils.CoroutineUtils.launch
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import redis.clients.jedis.exceptions.JedisException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
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
) : Subscriber<P, S>(namespace, name) {

    val channel = "$namespace:$name"
    private var subJedis: Jedis = redis.resource
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
                if (channel != this@RedisSubscriber.channel) return
                try {
                    val payloadWrapper = message.asPayloadWrapper<P>()
                    handleMessage(payloadWrapper)
                } catch (e: Exception) {
                    logger.log(Level.WARNING, "Error processing message: ${e.message}")
                }
            }
        }
    }

    private fun handleMessage(payloadWrapper: PayloadWrapper<P>) {
        when (payloadWrapper.state) {
            PayloadWrapper.State.PROXY -> handleProxyMessage(payloadWrapper)
            PayloadWrapper.State.FORWARD -> handleForwardMessage(payloadWrapper)
            PayloadWrapper.State.RESPOND -> {} // Handle response if needed
        }
    }

    private fun handleProxyMessage(payloadWrapper: PayloadWrapper<P>) {
        val isVelocity = runCatching { Class.forName("com.velocitypowered.api.proxy.ProxyServer") != null }.isSuccess
        if (!isVelocity) return

        launch(dispatcher) {
            val result = (HANDLER_LIST.find { it.namespace == namespace && it.name == name } as Subscriber<P, S>)
                .onSubscribe(payloadWrapper.typedPayload(payloadClass))
            result.await()
            publish(
                PayloadWrapper(
                    payloadWrapper.uniqueId,
                    result.getCompleted(),
                    PayloadWrapper.State.RESPOND,
                    payloadWrapper.source
                )
            )
        }
    }

    private fun handleForwardMessage(payloadWrapper: PayloadWrapper<P>) {
        if (payloadWrapper.excludeSource && isSource(payloadWrapper.uniqueId)) return
        launch(dispatcher) {
            val result = (HANDLER_LIST.find { it.namespace == namespace && it.name == name } as? Subscriber<P, S>)
                ?.onSubscribe(payloadWrapper.typedPayload(payloadClass))
            if (payloadWrapper.target == "PROCESSED") return@launch
            publish(
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
                        Thread.sleep(5000)
                    } catch (e: Exception) {
                        logger.log(Level.SEVERE, "Unexpected error in subscriber: ${e.message}")
                        isSubscribed.set(false)
                        safeCloseJedis()
                        Thread.sleep(5000)
                    }
                }
            }, "redis-sub-thread-${channel}-${UUID.randomUUID().toString().split("-").first()}")
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

    private suspend fun publish(payload: PayloadWrapper<*>) {
        val publication = CompletableDeferred<Unit>()
        launch(dispatcher) {
            delay(TIMEOUT_SECONDS * 1000)
            if (publication.isActive) {
                publication.completeExceptionally(IllegalStateException("Failed to publish payload in subscriber after $TIMEOUT_SECONDS seconds. Payload: $payload (channel: ${id()})"))
            }
        }

        val localJedis = redis.resource
        try {
            localJedis.publish(channel.toByteArray(), payload.asJson().toByteArray())
            publication.complete(Unit)
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error publishing message: ${e.message}")
            publication.completeExceptionally(e)
        } finally {
            localJedis.close()
        }
    }

    fun isSource(uniqueId: UUID): Boolean {
        return Publisher.HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.contains(uniqueId)
    }

    fun shutdown() {
        shouldReconnect.set(false)
        pubSub.unsubscribe()
        safeCloseJedis()
        subscriberThread?.interrupt()
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L
    }
}