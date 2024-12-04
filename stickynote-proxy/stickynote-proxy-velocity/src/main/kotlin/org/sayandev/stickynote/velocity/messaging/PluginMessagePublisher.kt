package org.sayandev.stickynote.velocity.messaging

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import com.velocitypowered.api.proxy.server.RegisteredServer
import kotlinx.coroutines.CompletableDeferred
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher
import org.sayandev.stickynote.velocity.StickyNote
import org.sayandev.stickynote.velocity.registerListener
import org.sayandev.stickynote.velocity.server

abstract class PluginMessagePublisher<P, S>(
    namespace: String,
    name: String,
    val resultClass: Class<S>
): Publisher<P, S>(
    StickyNote.javaLogger,
    namespace,
    name
) {

    val channelIdentifier = MinecraftChannelIdentifier.create(namespace, name)

    init {
        registerChannel()
    }

    private fun registerChannel() {
        server.channelRegistrar.register(channelIdentifier)
        registerListener(this)
    }

    fun publish(server: RegisteredServer, payloadWrapper: PayloadWrapper<P>): CompletableDeferred<S> {
        server.sendPluginMessage(channelIdentifier, payloadWrapper.asJson().toByteArray())
        return publish(payloadWrapper)
    }

    fun publish(player: Player, payloadWrapper: PayloadWrapper<P>): CompletableDeferred<S> {
        player.sendPluginMessage(channelIdentifier, payloadWrapper.asJson().toByteArray())
        return publish(payloadWrapper)
    }

    abstract fun handle(payload: P): S?

    @Subscribe
    fun onMessageReceived(event: PluginMessageEvent) {
        val data = event.data
        val channel = event.identifier.id
        if (channel != channelIdentifier.id) return
        val result = String(data, Charsets.UTF_8).asPayloadWrapper<S>()
        when (result.state) {
            PayloadWrapper.State.RESPOND -> {
                for (publisher in HANDLER_LIST.filterIsInstance<PluginMessagePublisher<P, S>>()) {
                    if (publisher.id() == channel) {
                        publisher.payloads[result.uniqueId]?.apply {
                            this.complete(result.typedPayload(resultClass))
                            publisher.payloads.remove(result.uniqueId)
                        } /*?: throw IllegalStateException("No payload found for uniqueId ${result.uniqueId}")*/
                    }
                }
            }
            PayloadWrapper.State.PROXY -> {}
            else -> {
                throw IllegalStateException("a result payload has been received with ${result.state} state, but it doesn't belong here. (payload: ${result})")
            }
        }
    }
}