package org.sayandev.stickynote.bukkit.nms.npc.entity

import org.bukkit.Location
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.npc.EntityNPC
import org.sayandev.stickynote.bukkit.nms.npc.NPCType
import org.sayandev.stickynote.bukkit.nms.utils.Rotations
import org.sayandev.stickynote.bukkit.nms.accessors.ArmorStandAccessor
import org.sayandev.stickynote.bukkit.utils.ServerVersion

class ArmorStandNPC(
    location: Location
): EntityNPC(
    if (ServerVersion.supports(13)) ArmorStandAccessor.CONSTRUCTOR_0!!.newInstance(NPCType.ARMOR_STAND.nmsEntityType(), NMSUtils.getServerLevel(location.world))
    else ArmorStandAccessor.CONSTRUCTOR_1!!.newInstance(NMSUtils.getServerLevel(location.world), location.x, location.y, location.z),
    location,
    NPCType.ARMOR_STAND
) {

    fun setHeadPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_HEAD_POSE!!.invoke(entity, rotations.toNmsRotations())
        sendEntityData()
    }

    fun getHeadPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_HEAD_POSE!!.invoke(entity))
    }

    fun setBodyPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_BODY_POSE!!.invoke(entity, rotations.toNmsRotations())
        sendEntityData()
    }

    fun getBodyPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_BODY_POSE!!.invoke(entity))
    }

    fun setLeftArmPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_LEFT_ARM_POSE!!.invoke(entity, rotations.toNmsRotations())
        sendEntityData()
    }

    fun getLeftArmPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_LEFT_ARM_POSE!!.invoke(entity))
    }

    fun setRightArmPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_RIGHT_ARM_POSE!!.invoke(entity, rotations.toNmsRotations())
        sendEntityData()
    }

    fun getRightArmPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_RIGHT_ARM_POSE!!.invoke(entity))
    }

    fun setLeftLegPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_LEFT_LEG_POSE!!.invoke(entity, rotations.toNmsRotations())
        sendEntityData()
    }

    fun getLeftLegPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_LEFT_LEG_POSE!!.invoke(entity))
    }

    fun setRightLegPose(rotations: Rotations) {
        ArmorStandAccessor.METHOD_SET_RIGHT_LEG_POSE!!.invoke(entity, rotations.toNmsRotations())
        sendEntityData()
    }

    fun getRightLegPose(): Rotations {
        return Rotations.fromNmsRotations(ArmorStandAccessor.METHOD_GET_RIGHT_LEG_POSE!!.invoke(entity))
    }

    fun setMarker(marker: Boolean) {
        ArmorStandAccessor.METHOD_SET_MARKER!!.invoke(entity, marker)
        sendEntityData()
    }

    fun isMarker(): Boolean {
        return ArmorStandAccessor.METHOD_IS_MARKER!!.invoke(entity) as Boolean
    }

    fun setNoBasePlate(noBasePlate: Boolean) {
        ArmorStandAccessor.METHOD_SET_NO_BASE_PLATE!!.invoke(entity, noBasePlate)
        sendEntityData()
    }

    fun hasBasePlate(): Boolean {
        return ArmorStandAccessor.METHOD_IS_NO_BASE_PLATE!!.invoke(entity) as Boolean
    }

    fun setShowArms(showArms: Boolean) {
        ArmorStandAccessor.METHOD_SET_SHOW_ARMS!!.invoke(entity, showArms)
        sendEntityData()
    }

    fun isShowingArms(): Boolean {
        return ArmorStandAccessor.METHOD_IS_SHOW_ARMS!!.invoke(entity) as Boolean
    }

    fun setSmall(small: Boolean) {
        ArmorStandAccessor.METHOD_SET_SMALL!!.invoke(entity, small)
        sendEntityData()
    }

    fun isSmall(): Boolean {
        return ArmorStandAccessor.METHOD_IS_SMALL!!.invoke(entity) as Boolean
    }

    fun setYBodyRotation(yBodyRotation: Float) {
        ArmorStandAccessor.METHOD_SET_YBODY_ROT!!.invoke(entity, yBodyRotation)
        sendEntityData()
    }

    fun setYHeadRotation(yHeadRotation: Float) {
        ArmorStandAccessor.METHOD_SET_YHEAD_ROT!!.invoke(entity, yHeadRotation)
        sendEntityData()
    }

}