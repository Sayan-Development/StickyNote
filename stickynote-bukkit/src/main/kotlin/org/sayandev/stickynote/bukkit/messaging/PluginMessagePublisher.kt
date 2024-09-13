package org.sayandev.stickynote.bukkit.messaging

import com.google.gson.ExclusionStrategy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CompletableDeferred
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.sayandev.stickynote.bukkit.onlinePlayers
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.warn
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.Publisher

abstract class PluginMessagePublisher<P, S>(
    val namespace: String,
    channel: String,
    val payloadClass: Class<P>,
    val resultClass: Class<S>
): Publisher<P, S>(
    channel
), PluginMessageListener {

    init {
        registerChannel()
    }

    private fun registerChannel() {
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "$namespace:$channel")
        plugin.server.messenger.registerIncomingPluginChannel(plugin, "$namespace:$channel", this)
    }

    fun publish(player: Player, payloadWrapper: PayloadWrapper<P>): CompletableDeferred<S> {
        warn("calling send plugin message from player: ${player.name} and channel: ${namespace}:${channel}. payload: \n${Gson().toJson(payloadWrapper)}")
        player.sendPluginMessage(plugin, "$namespace:$channel", Gson().toJson(payloadWrapper, PayloadWrapper::class.java).toByteArray())
        return publish(payloadWrapper)
    }

    abstract fun handle(payload: P): S?

    override fun onPluginMessageReceived(channel: String, player: Player, data: ByteArray) {
        val result = Gson().fromJson<PayloadWrapper<S>>(String(data), PayloadWrapper::class.java)
        when (result.state) {
            PayloadWrapper.State.FORWARD -> {
                val payload = Gson().fromJson<PayloadWrapper<P>>(String(data), PayloadWrapper::class.java)
                // Can't just use Gson.fromJson(payload.payload) it thinks `P` is a LinkedTreeMap (generic issues)
                val result = handle(Gson().fromJson(Gson().toJson(payload.payload), payloadClass)) ?: return
                player.sendPluginMessage(plugin, channel, Gson().toJson(PayloadWrapper(payload.uniqueId, result, PayloadWrapper.State.RESPOND, payload.source), PayloadWrapper::class.java).toByteArray())
            }
            PayloadWrapper.State.RESPOND -> {
                warn("received from velocity 1 channel: ${channel}")
                for (publisher in HANDLER_LIST.filterIsInstance<PluginMessagePublisher<P, S>>()) {
                    warn("received from velocity 2 publisher: ${publisher}")
                    if ("${publisher.namespace}:${publisher.channel}" == channel) {
                        warn("received from velocity 3: ${result}")
                        publisher.payloads[result.uniqueId]?.apply {
                            warn("received from velocity 4")
                            this.complete(result.payload)
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