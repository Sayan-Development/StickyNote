package org.sayandev.stickynote.paper.nms.event

import org.bukkit.entity.Player

interface PacketListener {
    fun register()

    fun unregister()

    fun handle(player: Player, packet: Any)
}
