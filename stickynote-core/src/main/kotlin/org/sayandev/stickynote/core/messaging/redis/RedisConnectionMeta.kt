package org.sayandev.stickynote.core.messaging.redis

import kotlinx.coroutines.CoroutineDispatcher
import org.sayandev.stickynote.core.messaging.ConnectionMeta
import redis.clients.jedis.JedisPool

data class RedisConnectionMeta(
    val pool: JedisPool,
    val dispatcher: CoroutineDispatcher,
    override val timeoutMillis: Long = 5000L
): ConnectionMeta