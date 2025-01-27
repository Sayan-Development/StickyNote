package org.sayandev.stickynote.bukkit.messaging.subscriber

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.sayandev.stickynote.bukkit.messaging.publisher.PluginMessagePublisher
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.warn
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asOptionalPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.publisher.Publisher.Companion.HANDLER_LIST

class PluginMessageSubscribeListener<P, S>(
    val namespace: String,
    val name: String,
    val payloadClass: Class<P>,
    val resultClass: Class<S>,
    val publisher: PluginMessagePublisher<P, S>?
): PluginMessageListener {

    fun id(): String {
        return "$namespace:$name"
    }

    init {
        plugin.server.messenger.registerIncomingPluginChannel(plugin, this.id(), this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, data: ByteArray) {
        val result = String(data).asPayloadWrapper<S>()
        when (result.state) {
            PayloadWrapper.State.FORWARD -> {
                if (publisher != null) {
                    val wrappedPayload = String(data).asPayloadWrapper<P>()
                    val payloadResult = publisher.handle(wrappedPayload.typedPayload(payloadClass)) ?: return
                    player.sendPluginMessage(plugin, channel, PayloadWrapper(wrappedPayload.uniqueId, payloadResult, PayloadWrapper.State.RESPOND, wrappedPayload.source).asJson().toByteArray())
                } else {
                    throw IllegalStateException("tried to handle a payload with state ${result.state}, but it doesn't have a publisher")
                }
            }
            PayloadWrapper.State.RESPOND -> {
                for (publisher in HANDLER_LIST.filterIsInstance<PluginMessagePublisher<P, S>>()) {
                    if (publisher.id() == channel) {
                        val handle = String(data).asOptionalPayloadWrapper<P>()?.typedPayload(payloadClass)?.let { publisher.handle(it) }
                        publisher.payloads[result.uniqueId]?.apply {
                            if (handle != null) {
                                this.complete(result.typedPayload(resultClass))
                            }
                            publisher.payloads.remove(result.uniqueId)
                        } ?: throw IllegalStateException("No payload found for uniqueId ${result.uniqueId}")
                    }
                }
            }
            else -> {
                throw IllegalStateException("a result payload has been received with ${result.state} state, but it doesn't belong here. (payload: ${result})")
            }
        }
    }

}