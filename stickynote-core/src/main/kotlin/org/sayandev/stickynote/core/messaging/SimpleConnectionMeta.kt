package org.sayandev.stickynote.core.messaging

data class SimpleConnectionMeta(
    override val timeoutMillis: Long = 10000L,
) : ConnectionMeta