package org.sayandev.stickynote.bukkit.nms.npc.entity

import org.bukkit.Location
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.accessors.DisplayAccessor
import org.sayandev.stickynote.bukkit.nms.npc.EntityNPC
import org.sayandev.stickynote.bukkit.nms.npc.NPCType

open class DisplayNPC(
    location: Location,
    type: NPCType
): EntityNPC(
    DisplayAccessor.CONSTRUCTOR_0!!.newInstance(type.nmsEntityType, NMSUtils.getServerLevel(location.world)),
    location,
    type
) {
    //TODO
}