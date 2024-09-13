package org.sayandev.stickynote.bukkit.messaging

import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.Publisher

abstract class PluginMessagePublisher<P, S>(
    val namespace: String,
    channel: String
): Publisher<P, S>(
    channel
) {
    fun publish(player: Player, payload: P): CompletableDeferred<S> {
        player.sendPluginMessage(plugin, "$namespace:$channel", Gson().toJson(PayloadWrapper(payload)).toByteArray())
        return publish(payload)
    }
}