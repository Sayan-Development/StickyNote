package org.sayandev.stickynote.core.messaging.redis

import kotlinx.coroutines.CoroutineDispatcher
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import org.sayandev.stickynote.core.messaging.publisher.RedisPublisher
import org.sayandev.stickynote.core.messaging.subscriber.RedisSubscriber
import org.sayandev.stickynote.core.messaging.subscriber.Subscriber
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import redis.clients.jedis.exceptions.JedisException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
import java.util.logging.Logger

object RedisConnectionManager {
    private val namespaceSubscriptions = ConcurrentHashMap<String, NamespaceSubscription>()
    private val logger = Logger.getLogger(RedisConnectionManager::class.java.name)

    fun registerNamespace(
        namespace: String,
        redis: JedisPool,
        dispatcher: CoroutineDispatcher
    ) {
        namespaceSubscriptions.computeIfAbsent(namespace) {
            NamespaceSubscription(namespace, redis, dispatcher)
        }
    }

    fun unregisterNamespace(namespace: String) {
        namespaceSubscriptions[namespace]?.shutdown()
        namespaceSubscriptions.remove(namespace)
    }

    fun getSubscription(namespace: String): NamespaceSubscription? {
        return namespaceSubscriptions[namespace]
    }

    fun shutdown() {
        namespaceSubscriptions.values.forEach { it.shutdown() }
        namespaceSubscriptions.clear()
    }

    class NamespaceSubscription(
        private val namespace: String,
        private val redis: JedisPool,
        private val dispatcher: CoroutineDispatcher
    ) {
        private val pattern = "$namespace:*"
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
                override fun onPMessage(pattern: String, channel: String, message: String) {
                    if (!channel.startsWith("$namespace:")) return

                    try {
                        val channelName = channel.substringAfter("$namespace:")
                        handleMessage(channelName, message)
                    } catch (e: Exception) {
                        logger.log(Level.WARNING, "Error processing message on channel $channel: ${e.message}")
                    }
                }
            }
        }

        private fun handleMessage(channelName: String, message: String) {
            try {
                val payloadWrapper = message.asPayloadWrapper<Any>()

                when (payloadWrapper.state) {
                    PayloadWrapper.State.FORWARD -> handleForwardMessage(channelName, payloadWrapper)
                    PayloadWrapper.State.RESPOND -> handleResponseMessage(channelName, payloadWrapper)
                    PayloadWrapper.State.PROXY -> handleProxyMessage(channelName, payloadWrapper)
                }
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Error parsing message: ${e.message}")
            }
        }

        private fun handleForwardMessage(channelName: String, payloadWrapper: PayloadWrapper<Any>) {
            // Find matching publisher
            Publisher.HANDLER_LIST
                .filterIsInstance<RedisPublisher<*, *>>()
                .find { it.namespace == namespace && it.name == channelName }
                ?.handleForwardMessage(payloadWrapper)

            // Find matching subscriber
            Subscriber.HANDLER_LIST
                .filterIsInstance<RedisSubscriber<*, *>>()
                .find { it.namespace == namespace && it.name == channelName }
                ?.handleForwardMessage(payloadWrapper)
        }

        private fun handleResponseMessage(channelName: String, payloadWrapper: PayloadWrapper<Any>) {
            Publisher.HANDLER_LIST
                .filterIsInstance<RedisPublisher<*, *>>()
                .filter { it.namespace == namespace && it.name == channelName }
                .forEach { it.handleResponseMessage(payloadWrapper) }
        }

        private fun handleProxyMessage(channelName: String, payloadWrapper: PayloadWrapper<Any>) {
            Subscriber.HANDLER_LIST
                .filterIsInstance<RedisSubscriber<*, *>>()
                .find { it.namespace == namespace && it.name == channelName }
                ?.handleProxyMessage(payloadWrapper)
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
                            subJedis.psubscribe(pubSub, pattern)
                        } catch (e: JedisException) {
                            logger.log(Level.WARNING, "Redis connection lost for namespace $namespace: ${e.message}")
                            isSubscribed.set(false)
                            safeCloseJedis()
                            Thread.sleep(5000)
                        } catch (e: Exception) {
                            logger.log(Level.SEVERE, "Unexpected error in subscriber for namespace $namespace: ${e.message}")
                            isSubscribed.set(false)
                            safeCloseJedis()
                            Thread.sleep(5000)
                        }
                    }
                }, "redis-namespace-sub-$namespace-${UUID.randomUUID().toString().split("-").first()}")
                subscriberThread?.start()
            }
        }

        private fun safeCloseJedis() {
            try {
                subJedis.close()
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Error closing Jedis connection for namespace $namespace: ${e.message}")
            }
        }

        fun shutdown() {
            shouldReconnect.set(false)
            pubSub.punsubscribe()
            safeCloseJedis()
            subscriberThread?.interrupt()
        }
    }
}
