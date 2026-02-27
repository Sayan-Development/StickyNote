package org.sayandev.stickynote.paper.nms.npc.entity

import org.bukkit.Location
import org.sayandev.stickynote.paper.nms.NMSUtils
import org.sayandev.stickynote.paper.nms.accessors.ZombieAccessor
import org.sayandev.stickynote.paper.nms.npc.LivingEntityNPC
import org.sayandev.stickynote.paper.nms.npc.NPCType

class ZombieNPC(
    location: Location
): LivingEntityNPC(
    ZombieAccessor.CONSTRUCTOR_1!!.newInstance(NMSUtils.getServerLevel(location.world)),
    location,
    NPCType.ZOMBIE
)