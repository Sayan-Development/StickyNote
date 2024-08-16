package org.sayandev.stickynote.bukkit.nms.hologram

import org.bukkit.Location
import org.sayandev.stickynote.bukkit.nms.npc.NPC

abstract class HoloLine(
    val distance: Float
) {

    internal lateinit var npc: NPC

    fun isInitialized(): Boolean = this::npc.isInitialized

    internal abstract fun initializeNPC(location: Location)

}