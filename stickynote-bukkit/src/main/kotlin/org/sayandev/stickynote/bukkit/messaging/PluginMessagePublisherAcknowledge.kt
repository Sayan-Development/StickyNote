package org.sayandev.stickynote.bukkit.messaging

import com.google.gson.Gson
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.Publisher

class PluginMessagePublisherAcknowledge<S>(
    val namespace: String,
    val channel: String,
    val resultClass: Class<S>
): PluginMessageListener {

    init {
        plugin.server.messenger.registerIncomingPluginChannel(plugin, "$namespace:$channel", this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, data: ByteArray) {
        for (publisher in Publisher.HANDLER_LIST.filterIsInstance<PluginMessagePublisher<*, S>>()) {
            if (publisher.channel == channel) {
                val result = Gson().fromJson<PayloadWrapper<S>>(String(data), PayloadWrapper::class.java)
                publisher.payloads[result.uniqueId]?.apply {
                    this.complete(result.payload)
                    publisher.payloads.remove(result.uniqueId)
                } ?: throw IllegalStateException("No payload found for uniqueId ${result.uniqueId}")
            }
        }
    }

}