package org.sayandev.stickynote.bukkit.nms.hologram

import org.bukkit.Location
import org.sayandev.stickynote.bukkit.nms.npc.NPC
import java.util.UUID

abstract class HoloLine(
    val distance: Float
) {

    val uniqueId: UUID = UUID.randomUUID()
    internal lateinit var npc: NPC

    fun isInitialized(): Boolean = this::npc.isInitialized

    internal abstract fun initializeNPC(location: Location)

}