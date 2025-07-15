package org.sayandev.stickynote.core.messaging.redis

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.PayloadBehaviour
import org.sayandev.stickynote.core.messaging.PayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.Publisher
import org.sayandev.stickynote.core.utils.CoroutineUtils
import redis.clients.jedis.JedisPubSub
import redis.clients.jedis.exceptions.JedisConnectionException
import java.util.*
import java.util.logging.Logger

open class RedisPublisher<P : Any, S : Any>(
    messageMeta: MessageMeta<P, S>,
    connectionMeta: RedisConnectionMeta,
    logger: Logger
) : Publisher<RedisConnectionMeta, P, S>(
    messageMeta,
    connectionMeta,
    logger,
) {

    init {
        val pubSub = object : JedisPubSub() {
            override fun onMessage(channel: String, message: String) {
                if (channel != this@RedisPublisher.messageMeta.id()) return
                val result = message.asPayloadWrapper<S>()
                when (result.behaviour) {
                    PayloadBehaviour.FORWARD -> {
                        val wrappedPayload = message.asPayloadWrapper<P>()
                        if (wrappedPayload.excludeSource && isSource(wrappedPayload.uniqueId)) return
                        val payloadResult = handle(wrappedPayload.typedPayload(messageMeta.payloadType)) ?: return
                        connectionMeta.pool.resource.publish(
                            channel.toByteArray(),
                            PayloadWrapper(
                                wrappedPayload.uniqueId,
                                payloadResult,
                                PayloadBehaviour.RESPONSE,
                                wrappedPayload.source,
                                wrappedPayload.target,
                                wrappedPayload.excludeSource
                            ).asJson().toByteArray()
                        )
                    }
                    PayloadBehaviour.RESPONSE -> {
                        for (publisher in HANDLER_LIST.filterIsInstance<RedisPublisher<P, S>>()) {
                            if (publisher.messageMeta.id() == channel) {
                                publisher.payloads[result.uniqueId]?.apply {
                                    this.complete(result.typedPayload(messageMeta.resultType))
                                    publisher.payloads.remove(result.uniqueId)
                                }
                            }
                        }
                    }
                    else -> { }
                }
            }
        }
        Thread({
            while (true) {
                try {
                    connectionMeta.pool.resource.use { jedis ->
                        jedis.subscribe(pubSub, messageMeta.id())
                    }
                } catch (e: JedisConnectionException) {
                    logger.severe("Redis connection lost for channel ${messageMeta.id()}: ${e.message}. Retrying in ${connectionMeta.timeoutMillis} milliseconds...")
                    Thread.sleep(connectionMeta.timeoutMillis)
                } catch (e: Exception) {
                    logger.severe("Unexpected error in RedisPublisher: ${e.message}")
                    Thread.sleep(connectionMeta.timeoutMillis)
                }
            }
        }, "redis-pub-sub-thread-${messageMeta.id()}-${UUID.randomUUID().toString().split("-").first()}").start()
    }

    override suspend fun publish(payload: PayloadWrapper<P>): CompletableDeferred<S> {
        if (!HANDLER_LIST.contains(this)) {
            throw IllegalStateException("Publisher with id ${messageMeta.id()} is not registered")
        }

        val result = super.publish(payload)

        CoroutineUtils.launch(connectionMeta.dispatcher) {
            delay(connectionMeta.timeoutMillis)
            if (result.isActive) {
                result.completeExceptionally(IllegalStateException("Sent payload has not been responded in ${connectionMeta.timeoutMillis}ms. Payload: $payload (id: ${messageMeta.id()})"))
            }
            payloads.remove(payload.uniqueId)
        }

        CoroutineUtils.launch(connectionMeta.dispatcher) {
            connectionMeta.pool.resource.publish(messageMeta.id().toByteArray(), payload.asJson().toByteArray())
        }

        return result
    }
}