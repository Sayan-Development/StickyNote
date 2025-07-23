package org.sayandev.stickynote.bukkit.nms.npc.entity

import org.bukkit.Location
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.accessors.ZombieAccessor
import org.sayandev.stickynote.bukkit.nms.npc.LivingEntityNPC
import org.sayandev.stickynote.bukkit.nms.npc.NPCType

class ZombieNPC(
    location: Location
): LivingEntityNPC(
    ZombieAccessor.CONSTRUCTOR_1!!.newInstance(NMSUtils.getServerLevel(location.world)),
    location,
    NPCType.ZOMBIE
)