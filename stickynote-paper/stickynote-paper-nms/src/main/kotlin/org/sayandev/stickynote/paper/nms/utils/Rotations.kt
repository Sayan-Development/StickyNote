package org.sayandev.stickynote.paper.nms.utils

import org.sayandev.stickynote.paper.nms.accessors.RotationsAccessor

class Rotations(
    x: Float,
    y: Float,
    z: Float
) {

    val x: Float = if (!x.isInfinite() && !x.isNaN()) x % 360.0f else 0.0f
    val y: Float = if (!y.isInfinite() && !y.isNaN()) y % 360.0f else 0.0f
    val z: Float = if (!z.isInfinite() && !z.isNaN()) z % 360.0f else 0.0f

    fun toNmsRotations(): Any {
        return RotationsAccessor.CONSTRUCTOR_0!!.newInstance(x, y, z)
    }

    companion object {
        fun fromNmsRotations(nmsRotations: Any): Rotations {
            return Rotations(
                RotationsAccessor.METHOD_GET_X!!.invoke(nmsRotations) as Float,
                RotationsAccessor.METHOD_GET_Y!!.invoke(nmsRotations) as Float,
                RotationsAccessor.METHOD_GET_Z!!.invoke(nmsRotations) as Float
            )
        }
    }

}