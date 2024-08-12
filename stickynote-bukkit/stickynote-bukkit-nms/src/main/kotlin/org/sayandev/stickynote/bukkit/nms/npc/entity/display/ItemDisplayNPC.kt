package org.sayandev.stickynote.bukkit.nms.npc.entity.display

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.nms.NMSUtils.toNmsItemStack
import org.sayandev.stickynote.bukkit.nms.accessors.Display_ItemDisplayAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.SynchedEntityDataAccessor
import org.sayandev.stickynote.bukkit.nms.npc.NPCType

class ItemDisplayNPC(
    location: Location,
    item: ItemStack
): DisplayNPC(
    location,
    NPCType.ITEM_DISPLAY
) {

    var item: ItemStack = item
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_ItemDisplayAccessor.FIELD_DATA_ITEM_STACK_ID,
                value.toNmsItemStack()
            )
        }

    var itemDisplayContext: ItemDisplayContext = ItemDisplayContext.NONE
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_ItemDisplayAccessor.FIELD_DATA_ITEM_DISPLAY_ID,
                value.getId()
            )
        }

}