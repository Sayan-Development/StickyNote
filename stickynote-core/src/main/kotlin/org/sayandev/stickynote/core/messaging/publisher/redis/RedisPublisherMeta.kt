package org.sayandev.stickynote.core.messaging.publisher.redis

import kotlinx.coroutines.CoroutineDispatcher
import org.sayandev.stickynote.core.messaging.publisher.PublisherMeta
import redis.clients.jedis.JedisPool

data class RedisPublisherMeta(
    val pool: JedisPool,
    val dispatcher: CoroutineDispatcher,
): PublisherMeta