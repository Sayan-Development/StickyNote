package org.sayandev.stickynote.paper.messaging.subscriber

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.sayandev.stickynote.paper.messaging.publisher.PluginMessagePublisher
import org.sayandev.stickynote.paper.plugin
import org.sayandev.stickynote.paper.warn
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.PayloadBehaviour
import org.sayandev.stickynote.core.messaging.PayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.Publisher.Companion.HANDLER_LIST

class PluginMessageSubscribeListener<P : Any, R : Any>(
    val messageMeta: MessageMeta<P, R>,
    val publisher: PluginMessagePublisher<P, R>?
): PluginMessageListener {
    init {
        plugin.server.messenger.registerIncomingPluginChannel(plugin, messageMeta.id(), this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, data: ByteArray) {
        val result = String(data).asPayloadWrapper<R>()
        when (result.behaviour) {
            PayloadBehaviour.FORWARD -> {
                if (publisher != null) {
                    val wrappedPayload = String(data).asPayloadWrapper<P>()
                    val payloadResult = publisher.handle(wrappedPayload.typedPayload(messageMeta.payloadType)) ?: return
                    player.sendPluginMessage(plugin, channel, PayloadWrapper(wrappedPayload.uniqueId, payloadResult, PayloadBehaviour.RESPONSE, wrappedPayload.source).asJson().toByteArray())
                } else {
                    throw IllegalStateException("tried to handle a payload with state ${result.behaviour}, but it doesn't have a publisher")
                }
            }
            PayloadBehaviour.RESPONSE -> {
                for (publisher in HANDLER_LIST.filterIsInstance<PluginMessagePublisher<P, R>>()) {
                    if (messageMeta.id() == channel) {
                        publisher.payloads[result.uniqueId]?.apply {
                            this.complete(result.typedPayload(messageMeta.resultType))
                            publisher.payloads.remove(result.uniqueId)
                        } ?: throw IllegalStateException("No payload found for uniqueId ${result.uniqueId}")
                    }
                }
            }
            else -> {
                throw IllegalStateException("a result payload has been received with ${result.behaviour} state, but it doesn't belong here. (payload: ${result})")
            }
        }
    }

}
