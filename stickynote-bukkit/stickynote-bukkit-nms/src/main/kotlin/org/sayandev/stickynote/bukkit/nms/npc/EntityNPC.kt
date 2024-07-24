package org.sayandev.stickynote.bukkit.nms.npc

import org.bukkit.Location
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.nms.NMSUtils.sendPacket
import org.sayandev.stickynote.bukkit.nms.PacketUtils
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.nms.accessors.EntityAccessor

abstract class EntityNPC(
    entity: Any,
    location: Location,
    val npcType: NPCType
): NPC() {

    init {
        EntityAccessor.METHOD_SET_POS!!.invoke(entity, location.x, location.y, location.z)
        EntityAccessor.METHOD_SET_ROT!!.invoke(entity, location.yaw, location.pitch)
        initialize(entity)
    }

    override fun addViewer(viewer: Player) {
        viewer.sendPacket(
            PacketUtils.getAddEntityPacket(entity),
            PacketUtils.getEntityDataPacket(entity)
        )
    }

    override fun removeViewer(viewer: Player) {
        viewer.sendPacket(
            PacketUtils.getRemoveEntitiesPacket(entityId)
        )
    }

}