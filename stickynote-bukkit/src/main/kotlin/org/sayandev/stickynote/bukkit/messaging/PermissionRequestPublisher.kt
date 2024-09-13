package org.sayandev.stickynote.bukkit.messaging

import kotlinx.coroutines.CompletableDeferred
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.core.messaging.PermissionRequest
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import java.util.*

class PermissionRequestPublisher: PluginMessagePublisher<PermissionRequest, Boolean>(
    plugin.name.lowercase(),
    "sosis",
    PermissionRequest::class.java,
    Boolean::class.java,
) {

    fun request(player: Player, permission: String): CompletableDeferred<Boolean> {
        return publish(player, PayloadWrapper(PermissionRequest(player.uniqueId, permission)))
    }

    override fun handle(payload: PermissionRequest): Boolean? {
        val player = Bukkit.getPlayer(payload.player) ?: return null
        return player.hasPermission(payload.permission)
    }

}