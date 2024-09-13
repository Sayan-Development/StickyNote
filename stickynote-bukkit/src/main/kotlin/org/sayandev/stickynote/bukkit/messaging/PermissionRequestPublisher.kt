package org.sayandev.stickynote.bukkit.messaging

import kotlinx.coroutines.CompletableDeferred
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.core.messaging.PermissionRequest

class PermissionRequestPublisher: PluginMessagePublisher<PermissionRequest, Boolean>(
    plugin.name.lowercase(),
    "permission_request"
) {

    fun request(player: Player, permission: String): CompletableDeferred<Boolean> {
        return publish(PermissionRequest(player.uniqueId, permission))
    }

}