package org.sayandev.stickynote.bukkit.nms.hologram

import org.bukkit.Location
import org.sayandev.stickynote.bukkit.nms.npc.NPC

abstract class HoloLine(
    val distance: Float
) {

    lateinit var npc: NPC; protected set

    fun isInitialized(): Boolean = this::npc.isInitialized

    abstract fun initializeNPC(location: Location)

}