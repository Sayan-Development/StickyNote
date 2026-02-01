package org.sayandev.stickynote.core.messaging.websocket

import kotlinx.coroutines.CoroutineDispatcher
import org.sayandev.stickynote.core.messaging.ConnectionMeta
import java.net.URI

data class WebSocketConnectionMeta(
    val uri: URI,
    val dispatcher: CoroutineDispatcher,
    override val timeoutMillis: Long = 5000L
): ConnectionMeta