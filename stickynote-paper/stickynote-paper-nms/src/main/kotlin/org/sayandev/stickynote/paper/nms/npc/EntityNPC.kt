package org.sayandev.stickynote.paper.nms.npc

import org.bukkit.Location
import org.bukkit.entity.Player
import org.sayandev.stickynote.paper.nms.NMSUtils.sendPacketSync
import org.sayandev.stickynote.paper.nms.PacketUtils
import org.sayandev.stickynote.paper.nms.accessors.EntityAccessor

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
        viewer.sendPacketSync(
            PacketUtils.getAddEntityPacket(entity),
            PacketUtils.getEntityDataPacket(entity),
            PacketUtils.getEntityRotPacket(entityId, EntityAccessor.FIELD_Y_ROT!!.get(entity) as Float, EntityAccessor.FIELD_X_ROT!!.get(entity) as Float),
        )
    }

    override fun removeViewer(viewer: Player) {
        viewer.sendPacketSync(
            PacketUtils.getRemoveEntitiesPacket(entityId)
        )
    }

}