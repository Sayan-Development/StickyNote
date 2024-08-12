package org.sayandev.stickynote.bukkit.nms.npc.entity

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.nms.npc.NPCType

class ItemDisplayNPC(
    location: Location,
    item: ItemStack
): DisplayNPC(
    location,
    NPCType.ITEM_DISPLAY
) {



}