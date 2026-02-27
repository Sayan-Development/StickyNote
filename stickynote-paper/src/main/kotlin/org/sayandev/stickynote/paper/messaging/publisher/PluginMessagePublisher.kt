package org.sayandev.stickynote.paper.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import org.bukkit.entity.Player
import org.sayandev.stickynote.paper.messaging.subscriber.PluginMessageSubscribeListener
import org.sayandev.stickynote.paper.onlinePlayers
import org.sayandev.stickynote.paper.plugin
import org.sayandev.stickynote.paper.warn
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.PayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.Publisher
import org.sayandev.stickynote.core.messaging.SimpleConnectionMeta

abstract class PluginMessagePublisher<P : Any, R : Any>(
    messageMeta: MessageMeta<P, R>,
    connectionMeta: SimpleConnectionMeta,
    val withSubscriber: Boolean
): Publisher<SimpleConnectionMeta, P, R>(
    messageMeta,
    connectionMeta,
    plugin.logger
) {

    var subscriberListener: PluginMessageSubscribeListener<P, R>? = null

    override fun register() {
        super.register()
        registerChannel()
    }

    override fun unregister() {
        super.unregister()
        unregisterChannel()
    }

    private fun registerChannel() {
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, messageMeta.id())
        if (withSubscriber) {
            subscriberListener = PluginMessageSubscribeListener(messageMeta, this)
        }
    }

    private fun unregisterChannel() {
        plugin.server.messenger.unregisterOutgoingPluginChannel(plugin, messageMeta.id())
        if (withSubscriber) {
            plugin.server.messenger.unregisterIncomingPluginChannel(plugin, messageMeta.id())
        }
    }

    suspend fun publish(player: Player, payloadWrapper: PayloadWrapper<P>): CompletableDeferred<R> {
        player.sendPluginMessage(plugin, messageMeta.id(), payloadWrapper.asJson().toByteArray())
        return publish(payloadWrapper)
    }

    abstract override fun handle(payload: P): R?

}
