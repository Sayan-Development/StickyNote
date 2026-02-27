package org.sayandev.stickynote.paper.utils

import org.bukkit.Location
import kotlin.math.floor

object LocationUtils {
    fun toCenter(location: Location): Location {
        return Location(
            location.world,
            floor(location.x) + 0.5,
            floor(location.y) + 0.5,
            floor(location.z) + 0.5,
            location.yaw,
            location.pitch
        )
    }
}

fun Location.toCenter(): Location {
    return LocationUtils.toCenter(this)
}