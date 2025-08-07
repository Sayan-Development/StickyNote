package org.sayandev.stickynote.core.messaging.subscriber

import io.lettuce.core.RedisClient
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.pubsub.RedisPubSubListener
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

abstract class RedisSubscriber<P, S>(
    private val dispatcher: CoroutineContext,
    redisClient: RedisClient,
    namespace: String,
    name: String,
    private val payloadClass: Class<P>,
    private val logger: Logger
) : Subscriber<P, S>(namespace, name) {

    private val channel = "$namespace:$name"
    private val pubSubConnection: StatefulRedisPubSubConnection<String, String> = redisClient.connectPubSub()
    private val pubConnection = redisClient.connect()
    private val pubAsync: RedisAsyncCommands<String, String> = pubConnection.async()
    private val reactive: RedisPubSubReactiveCommands<String, String> = pubSubConnection.reactive()

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        startSubscriber()
    }

    private fun startSubscriber() {
        pubSubConnection.addListener(object : RedisPubSubListener<String, String> {
            override fun message(channel: String, message: String) {
                if (channel != this@RedisSubscriber.channel) return
                try {
                    val payloadWrapper = message.asPayloadWrapper<P>()
                    handleMessage(payloadWrapper)
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

    private fun handleMessage(payloadWrapper: PayloadWrapper<P>) {
        when (payloadWrapper.state) {
            PayloadWrapper.State.PROXY -> handleProxyMessage(payloadWrapper)
            PayloadWrapper.State.FORWARD -> handleForwardMessage(payloadWrapper)
            PayloadWrapper.State.RESPOND -> {} // Optional: implement response handling
        }
    }

    private fun handleProxyMessage(payloadWrapper: PayloadWrapper<P>) {
        val isVelocity = runCatching {
            Class.forName("com.velocitypowered.api.proxy.ProxyServer") != null
        }.isSuccess
        if (!isVelocity) return

        scope.launch {
            val result = (HANDLER_LIST.find { it.namespace == namespace && it.name == name } as Subscriber<P, S>)
                .onSubscribe(payloadWrapper.typedPayload(payloadClass)) ?: return@launch
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
        scope.launch {
            val subscriber = (HANDLER_LIST.find { it.namespace == namespace && it.name == name } as? Subscriber<P, S>)
            val result = onSubscribe(payloadWrapper.typedPayload(payloadClass))
            if (result == null && subscriber != null) return@launch
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

    private suspend fun publish(payload: PayloadWrapper<*>) {
        val publication = CompletableDeferred<Unit>()
        val job = scope.launch {
            delay(TIMEOUT_SECONDS * 1000)
            if (publication.isActive) {
                publication.completeExceptionally(
                    IllegalStateException(
                        "Failed to publish payload in subscriber after $TIMEOUT_SECONDS seconds. Payload: $payload (channel: ${id()})"
                    )
                )
            }
        }

        try {
            val published = pubAsync.publish(channel, payload.asJson()).await()
            if (published <= 0) return
            publication.complete(Unit)
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error publishing message: ${e.message}")
            publication.completeExceptionally(e)
        } finally {
            job.cancel()
        }
    }

    fun isSource(uniqueId: UUID): Boolean {
        return Publisher.HANDLER_LIST
            .asSequence()
            .flatMap { publisher -> publisher.payloads.keys.asSequence() }
            .contains(uniqueId)
    }

    fun shutdown() {
        try {
            runBlocking {
                reactive.unsubscribe(channel).awaitFirstOrNull()
            }
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error unsubscribing: ${e.message}")
        }

        try {
            pubSubConnection.close()
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
