package org.sayandev.stickynote.bukkit.nms.npc.entity

import org.bukkit.Location
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.npc.EntityNPC
import org.sayandev.stickynote.bukkit.nms.npc.NPCType
import org.sayandev.stickynote.bukkit.nms.utils.Rotations
import org.sayandev.stickynote.nms.accessors.ArmorStandAccessor

class ArmorStandNPC(
    location: Location
): EntityNPC(
    ArmorStandAccessor.CONSTRUCTOR_0!!.newInstance(NPCType.ARMOR_STAND.nmsEntityType, NMSUtils.getServerLevel(location.world)),
    location,
    NPCType.ARMOR_STAND
) {

    fun setHeadPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_HEAD_POSE!!.invoke(entity, rotations.toNmsRotations())
    }

    fun getHeadPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_HEAD_POSE!!.invoke(entity))
    }

    fun setBodyPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_BODY_POSE!!.invoke(entity, rotations.toNmsRotations())
    }

    fun getBodyPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_BODY_POSE!!.invoke(entity))
    }

    fun setLeftArmPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_LEFT_ARM_POSE!!.invoke(entity, rotations.toNmsRotations())
    }

    fun getLeftArmPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_LEFT_ARM_POSE!!.invoke(entity))
    }

    fun setRightArmPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_RIGHT_ARM_POSE!!.invoke(entity, rotations.toNmsRotations())
    }

    fun getRightArmPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_RIGHT_ARM_POSE!!.invoke(entity))
    }

    fun setLeftLegPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_LEFT_LEG_POSE!!.invoke(entity, rotations.toNmsRotations())
    }

    fun getLeftLegPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_LEFT_LEG_POSE!!.invoke(entity))
    }

    fun setRightLegPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_RIGHT_LEG_POSE!!.invoke(entity, rotations.toNmsRotations())
    }

    fun getRightLegPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_RIGHT_LEG_POSE!!.invoke(entity))
    }

    fun setMarker(marker: Boolean) {
        ArmorStandAccessor.METHOD_SET_MARKER!!.invoke(entity, marker)
    }

    fun isMarker(): Boolean {
        return ArmorStandAccessor.METHOD_IS_MARKER!!.invoke(entity) as Boolean
    }

    fun setNoBasePlate(noBasePlate: Boolean) {
        ArmorStandAccessor.METHOD_SET_NO_BASE_PLATE!!.invoke(entity, noBasePlate)
    }

    fun hasBasePlate(): Boolean {
        return ArmorStandAccessor.METHOD_IS_NO_BASE_PLATE!!.invoke(entity) as Boolean
    }

    fun setShowArms(showArms: Boolean) {
        ArmorStandAccessor.METHOD_SET_SHOW_ARMS!!.invoke(entity, showArms)
    }

    fun isShowingArms(): Boolean {
        return ArmorStandAccessor.METHOD_IS_SHOW_ARMS!!.invoke(entity) as Boolean
    }

    fun setSmall(small: Boolean) {
        ArmorStandAccessor.METHOD_SET_SMALL!!.invoke(entity, small)
    }

    fun isSmall(): Boolean {
        return ArmorStandAccessor.METHOD_IS_SMALL!!.invoke(entity) as Boolean
    }

    fun setYBodyRotation(yBodyRotation: Float) {
        ArmorStandAccessor.METHOD_SET_YBODY_ROT!!.invoke(entity, yBodyRotation)
    }

    fun setYHeadRotation(yHeadRotation: Float) {
        ArmorStandAccessor.METHOD_SET_YHEAD_ROT!!.invoke(entity, yHeadRotation)
    }

}