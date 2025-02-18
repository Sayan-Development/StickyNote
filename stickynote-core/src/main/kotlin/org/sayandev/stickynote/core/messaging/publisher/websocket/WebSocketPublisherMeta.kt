package org.sayandev.stickynote.core.messaging.publisher.websocket

import kotlinx.coroutines.CoroutineDispatcher
import org.sayandev.stickynote.core.messaging.publisher.PublisherMeta
import java.net.URI

data class WebSocketPublisherMeta(
    val dispatcher: CoroutineDispatcher,
    val uri: URI
): PublisherMeta