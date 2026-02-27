package org.sayandev.stickynote.paper.utils

object MathUtils {

    fun getAngle(yawOrPitch: Float): Byte {
        return (yawOrPitch * 256 / 360).toInt().toByte()
    }

}