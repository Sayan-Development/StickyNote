package org.sayandev.stickynote.bukkit.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.StickyNote
import org.sayandev.stickynote.bukkit.messaging.subscriber.PluginMessageSubscribeListener
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.Publisher

abstract class PluginMessagePublisher<P, S>(
    namespace: String,
    name: String,
    val payloadClass: Class<P>,
    val resultClass: Class<S>,
    val withSubscriber: Boolean
): Publisher<P, S>(
    StickyNote.logger,
    namespace,
    name
) {

    var subscriberListener: PluginMessageSubscribeListener<P, S>? = null

    init {
        register(this)
        registerChannel()
    }

    private fun registerChannel() {
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, this.id())
        if (withSubscriber) {
            subscriberListener = PluginMessageSubscribeListener(namespace, name, payloadClass, resultClass, this)
        }
    }

    private fun unregisterChannel() {
        plugin.server.messenger.unregisterOutgoingPluginChannel(plugin, this.id())
        if (withSubscriber) {
            plugin.server.messenger.unregisterIncomingPluginChannel(plugin, this.id())
        }
    }

    fun publish(player: Player, payloadWrapper: PayloadWrapper<P>): CompletableDeferred<S> {
        player.sendPluginMessage(plugin, this.id(), payloadWrapper.asJson().toByteArray())
        return publish(payloadWrapper)
    }

    abstract fun handle(payload: P): S?

}