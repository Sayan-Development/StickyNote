package org.sayandev.stickynote.core.messaging.redis

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.PayloadBehaviour
import org.sayandev.stickynote.core.messaging.PayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.Publisher
import org.sayandev.stickynote.core.messaging.Subscriber
import org.sayandev.stickynote.core.utils.CoroutineUtils
import org.sayandev.stickynote.core.utils.CoroutineUtils.awaitWithTimeout
import redis.clients.jedis.JedisPubSub
import redis.clients.jedis.exceptions.JedisConnectionException
import java.util.*
import java.util.logging.Logger

@OptIn(ExperimentalCoroutinesApi::class)
abstract class RedisSubscriber<P : Any, R : Any>(
    messageMeta: MessageMeta<P, R>,
    val connectionMeta: RedisConnectionMeta,
    val logger: Logger
) : Subscriber<P, R>(messageMeta) {

    init {
        val pubSub = object : JedisPubSub() {
            override fun onMessage(channel: String, message: String) {
                if (channel != messageMeta.id()) return
                val payloadWrapper = message.asPayloadWrapper<P>()

                when (payloadWrapper.behaviour) {
                    PayloadBehaviour.FORWARD_PROXY -> {
                        if (!isVelocity) return

                        CoroutineUtils.launch(connectionMeta.dispatcher) {
                            val result =
                                (HANDLER_LIST.find { it.messageMeta.id() == messageMeta.id() } as Subscriber<P, R>)
                                    .onSubscribe(payloadWrapper.typedPayload(messageMeta.payloadType))
                            result.await()
                            publishWithTimeout(
                                PayloadWrapper(
                                    payloadWrapper.uniqueId,
                                    result.getCompleted(),
                                    PayloadBehaviour.RESPONSE,
                                    payloadWrapper.source
                                )
                            )
                        }
                    }
                    PayloadBehaviour.FORWARD -> {
                        if (payloadWrapper.excludeSource && isSource(payloadWrapper.uniqueId)) return
                        CoroutineUtils.launch(connectionMeta.dispatcher) {
                            val result =
                                (HANDLER_LIST.find { it.messageMeta.id() == messageMeta.id() } as? Subscriber<P, R>)?.onSubscribe(
                                    payloadWrapper.typedPayload(messageMeta.payloadType)
                                )
                            if (payloadWrapper.target == "PROCESSED") return@launch;
                            publishWithTimeout(
                                PayloadWrapper(
                                    payloadWrapper.uniqueId,
                                    result?.getCompleted() ?: payloadWrapper.payload,
                                    if (result != null) PayloadBehaviour.RESPONSE else payloadWrapper.behaviour,
                                    payloadWrapper.source,
                                    "PROCESSED"
                                )
                            )
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
                    logger.severe("Unexpected error in RedisSubscriber: ${e.message}")
                    Thread.sleep(connectionMeta.timeoutMillis)
                }
            }
        }, "redis-sub-sub-thread-${messageMeta.id()}-${UUID.randomUUID().toString().split("-").first()}").start()
    }

    private suspend fun publishWithTimeout(payload: PayloadWrapper<*>) {
        val deferred = CompletableDeferred<Unit>()
        CoroutineUtils.launch(connectionMeta.dispatcher) {
            connectionMeta.pool.resource.publish(messageMeta.id().toByteArray(), payload.asJson().toByteArray())
            deferred.complete(Unit)
        }
        deferred.awaitWithTimeout(connectionMeta.timeoutMillis) {
            logger.warning("failed to publish payload `${payload}` within ${connectionMeta.timeoutMillis}ms.")
        }
    }

    fun isSource(uniqueId: UUID): Boolean {
        return Publisher.HANDLER_LIST.any { publisher -> publisher.payloads.containsKey(uniqueId) }
    }
}
