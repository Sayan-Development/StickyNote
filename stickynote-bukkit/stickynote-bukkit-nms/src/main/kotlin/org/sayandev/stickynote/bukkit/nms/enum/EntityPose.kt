package org.sayandev.stickynote.bukkit.nms.enum

import org.sayandev.stickynote.nms.accessors.PoseAccessor

enum class EntityPose(val nmsPose: Any) {
    STANDING(PoseAccessor.FIELD_STANDING!!),
    SLEEPING(PoseAccessor.FIELD_SLEEPING!!),
    CROUCHING(PoseAccessor.FIELD_CROUCHING!!),
    SWIMMING(PoseAccessor.FIELD_SWIMMING!!);
}