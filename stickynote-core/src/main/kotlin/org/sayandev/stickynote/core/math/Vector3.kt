package org.sayandev.stickynote.core.math

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

@ConfigSerializable
data class Vector3(
    var x: Double,
    var y: Double,
    var z: Double
) {

    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    val blockX: Int
        get() {
            if (x > 0) return x.toInt()
            if (x < 0) return floor(x).toInt()
            return 0
        }

    val blockY: Int
        get() {
            if (y > 0) return y.toInt()
            if (y < 0) return floor(y).toInt()
            return 0
        }

    val blockZ: Int
        get() {
            if (z > 0) return z.toInt()
            if (z < 0) return floor(z).toInt()
            return 0
        }

    val centerX: Double
        get() = blockX + 0.5

    val centerY: Double
        get() = blockY + 0.5

    val centerZ: Double
        get() = blockZ + 0.5

    fun add(x: Double, y: Double, z: Double): Vector3 {
        this.x += x
        this.y += y
        this.z += z

        return this
    }

    fun add(x: Int, y: Int, z: Int): Vector3 {
        this.x += x
        this.y += y
        this.z += z

        return this
    }

    fun add(other: Vector3): Vector3 {
        return add(other.x, other.y, other.z)
    }

    fun subtract(x: Double, y: Double, z: Double): Vector3 {
        this.x -= x
        this.y -= y
        this.z -= z

        return this
    }

    fun subtract(x: Int, y: Int, z: Int): Vector3 {
        this.x -= x
        this.y -= y
        this.z -= z

        return this
    }

    fun subtract(other: Vector3): Vector3 {
        return subtract(other.x, other.y, other.z)
    }

    fun distance(other: Vector3): Double {
        return sqrt((x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2))
    }

    fun length(): Double {
        return sqrt((x * x) + (y * y) + (z * z))
    }

    fun travelVector(to: Vector3): Vector3 {
        var xD: Double = abs(x - to.x)
        var yD: Double = abs(y - to.y)
        var zD: Double = abs(z - to.z)
        if (x > to.x) xD = -xD
        if (y > to.y) yD = -yD
        if (z > to.z) zD = -zD
        return at(xD, yD, zD)
    }

    override fun toString(): String {
        val x = if (this.x == blockX.toDouble()) "" + this.blockX else "" + this.x
        val y = if (this.y == blockY.toDouble()) "" + this.blockY else "" + this.y
        val z = if (this.z == blockZ.toDouble()) "" + this.blockZ else "" + this.z
        return String.format("(%s, %s, %s)", x, y, z)
    }

    override fun hashCode(): Int {
        return this.blockX xor (this.blockZ shl 12) xor (this.blockY shl 24)
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj !is Vector3) {
            false
        } else {
            this.x == obj.x && (this.y == obj.y) && (this.z == obj.z)
        }
    }

    companion object {
        private val ZERO = at(0, 0, 0)
        private val ONE = at(1, 1, 1)

        @JvmStatic
        val zero: Vector3
            get() = ZERO.copy()
        val one: Vector3
            get() = ONE.copy()

        @JvmStatic
        fun at(x: Double, y: Double, z: Double): Vector3 {
            return Vector3(x, y, z)
        }

        @JvmStatic
        fun at(x: Int, y: Int, z: Int): Vector3 {
            return Vector3(x.toDouble(), y.toDouble(), z.toDouble())
        }
    }
}