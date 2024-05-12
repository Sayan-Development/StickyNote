package org.sayandev.stickynote.bukkit.utils

object MathUtils {

    fun getAngle(yawOrPitch: Float): Byte {
        return (yawOrPitch * 256 / 360).toInt().toByte()
    }

}