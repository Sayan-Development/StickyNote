package org.sayandev.stickynote.core.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import org.sayandev.stickynote.core.coroutine.dispatcher.AsyncDispatcher
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.toPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.websocket.WebSocketPublisher
import org.sayandev.stickynote.core.messaging.publisher.websocket.WebSocketPublisherMeta
import java.net.URI
import java.util.logging.Logger

class Test : PublisherProvider<String, Boolean> {
    override val publishers: List<Publisher<*, String, Boolean>> = listOf(
        Publisher.create<WebSocketPublisher<String, Boolean>, WebSocketPublisherMeta, String, Boolean>(
            MessageMeta.create("namespace", "name"),
            WebSocketPublisherMeta(AsyncDispatcher("prefix", 10), URI.create("ws://localhost:8080")),
            Logger.getAnonymousLogger()
        )
    )

    suspend fun test(): CompletableDeferred<Boolean> {
        return publisher<WebSocketPublisher<String, Boolean>>()
            .publish("test".toPayloadWrapper())
    }
}