package org.sayandev.stickynote.paper.extension

import org.bukkit.Location
import org.bukkit.World
import org.sayandev.stickynote.core.math.Vector3

fun Vector3.toLocation(world: World?): Location {
    return Location(world, x, y, z)
}