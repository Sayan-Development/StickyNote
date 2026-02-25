package org.sayandev.stickynote.core.messaging.websocket

import kotlinx.coroutines.CoroutineDispatcher
import org.sayandev.stickynote.core.messaging.ConnectionMeta
import java.net.URI

data class WebSocketConnectionMeta(
    val uri: URI,
    val dispatcher: CoroutineDispatcher,
    val autoHostOnPort: Boolean = true,
    val hostWhenAutoHosting: String = "0.0.0.0",
    override val timeoutMillis: Long = 5000L
): ConnectionMeta
