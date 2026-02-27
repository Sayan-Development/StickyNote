package org.sayandev.stickynote.paper.extension

import org.bukkit.Location
import org.sayandev.stickynote.core.math.Vector3

fun Location.toVector3(): Vector3 {
    return Vector3(x, y, z)
}