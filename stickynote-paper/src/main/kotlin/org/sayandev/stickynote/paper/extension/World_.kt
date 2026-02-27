package org.sayandev.stickynote.paper.extension

import org.bukkit.World
import org.bukkit.block.Block
import org.sayandev.stickynote.core.math.Vector3

fun World.getBlockAt(vector3: Vector3): Block {
    return this.getBlockAt(vector3.blockX, vector3.blockY, vector3.blockZ)
}