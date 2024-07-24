package org.sayandev.stickynote.bukkit.nms.enum

import org.sayandev.stickynote.nms.accessors.PoseAccessor
import java.util.*

enum class Pose(val nmsPose: Any?, val bitMask: Int) {

    STANDING(PoseAccessor.FIELD_STANDING!!, 0),
    SLEEPING(PoseAccessor.FIELD_SLEEPING!!, 0),
    ON_FIRE(null, 0x01),
    CROUCHING(PoseAccessor.FIELD_CROUCHING!!, 0),
    SPRINTING(null, 0x08),
    SWIMMING(PoseAccessor.FIELD_SWIMMING!!, 0),
    INVISIBLE(null, 0x20),
    GLOWING(null, 0x40),
    ELYTRA_FLYING(null, 0x80);

    companion object {
        fun getBitMasks(vararg poses: Pose): Byte {
            return getBitMasks(poses.toList())
        }

        fun getBitMasks(poses: Collection<Pose>): Byte {
            var bitMask = 0
            for (pose in poses) {
                bitMask = bitMask or pose.bitMask
            }
            return bitMask.toByte()
        }

        fun getPoses(bitMask: Int): Set<Pose> {
            val poses: MutableSet<Pose> = EnumSet.noneOf(Pose::class.java)
            for (pose in entries) {
                if ((bitMask and pose.bitMask) == pose.bitMask) {
                    poses.add(pose)
                }
            }
            return poses
        }
    }
}