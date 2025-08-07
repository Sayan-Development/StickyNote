package org.sayandev.stickynote.core.messaging.publisher

import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubListener
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.utils.CoroutineUtils
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@OptIn(DelicateCoroutinesApi::class)
abstract class RedisPublisher<P, S>(
    val dispatcher: CoroutineDispatcher,
    redisClient: RedisClient,
    namespace: String,
    name: String,
    val payloadClass: Class<P>,
    val resultClass: Class<S>,
    logger: Logger
) : Publisher<P, S>(logger, namespace, name) {

    val channel = "$namespace:$name"
    private val connection: StatefulRedisPubSubConnection<String, String> =
        redisClient.connectPubSub()
    private val reactive: RedisPubSubReactiveCommands<String, String> =
        connection.reactive()
    private val pubConnection = redisClient.connect()

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        startSubscriber()
    }

    private fun startSubscriber() {
        connection.addListener(object : RedisPubSubListener<String, String> {
            override fun message(channel: String, message: String) {
                if (channel != this@RedisPublisher.channel) return

                try {
                    val result = message.asPayloadWrapper<S>()
                    when (result.state) {
                        PayloadWrapper.State.FORWARD -> handleForward(message)
                        PayloadWrapper.State.RESPOND -> handleResponse(result)
                        else -> {} // skip
                    }
                } catch (e: Exception) {
                    logger.log(Level.WARNING, "Error processing message: ${e.message}")
                }
            }

            override fun message(p0: String?, p1: String?, p2: String?) {}
            override fun subscribed(p0: String?, p1: Long) {}
            override fun unsubscribed(p0: String?, p1: Long) {}
            override fun psubscribed(p0: String?, p1: Long) {}
            override fun punsubscribed(p0: String?, p1: Long) {}
        })

        scope.launch {
            reactive.subscribe(channel).asFlow().collect()
        }
    }

    private fun handleForward(message: String) {
        val wrappedPayload = message.asPayloadWrapper<P>()
        if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return
        val payloadResult = handle(wrappedPayload.typedPayload(payloadClass)) ?: return

        try {
            pubConnection.async().publish(
                channel,
                PayloadWrapper(
                    wrappedPayload.uniqueId,
                    payloadResult,
                    PayloadWrapper.State.RESPOND,
                    wrappedPayload.source,
                    wrappedPayload.target,
                    wrappedPayload.excludeSource
                ).asJson()
            )
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error publishing response: ${e.message}")
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

    override suspend fun publish(payload: PayloadWrapper<P>): CompletableDeferred<S> {
        val result = super.publish(payload)

        CoroutineUtils.async(dispatcher) {
            try {
                val published = pubConnection.async().publish(channel, payload.asJson()).get()
                if (published <= 0) {
                    payloads.remove(payload.uniqueId)
                    return@async
                }
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Error during publish: ${e.message}")
                payloads.remove(payload.uniqueId)
                return@async
            }

            delay(TIMEOUT_SECONDS * 1000L)
            if (result.isActive) {
                result.completeExceptionally(IllegalStateException("No response received in $TIMEOUT_SECONDS seconds in channel: ${channel}"))
                payloads.remove(payload.uniqueId)
            }
        }

        return result
    }

    abstract fun handle(payload: P): S?

    fun isSource(uniqueId: UUID): Boolean {
        return HANDLER_LIST.asSequence()
            .flatMap { publisher -> publisher.payloads.keys.asSequence() }.contains(uniqueId)
    }

    fun shutdown() {
        runBlocking {
            reactive.unsubscribe(channel).awaitSingle()
        }

        try {
            connection.close()
            pubConnection.close()
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error closing Redis connections: ${e.message}")
        }

        scope.cancel()
    }

    companion object {
        const val TIMEOUT_SECONDS = 5L
    }
}
