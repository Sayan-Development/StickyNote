package org.sayandev.stickynote.bukkit.nms.npc.entity.display

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.NMSUtils.toNmsItemStack
import org.sayandev.stickynote.bukkit.nms.accessors.Display_ItemDisplayAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.SynchedEntityDataAccessor
import org.sayandev.stickynote.bukkit.nms.npc.NPCType

class ItemDisplayNPC(
    location: Location,
    display: ItemStack
): DisplayNPC(
    Display_ItemDisplayAccessor.CONSTRUCTOR_0!!.newInstance(NPCType.ITEM_DISPLAY.nmsEntityType, NMSUtils.getServerLevel(location.world)),
    location,
    NPCType.ITEM_DISPLAY
) {

    var item: ItemStack = display
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_ItemDisplayAccessor.FIELD_DATA_ITEM_STACK_ID,
                value.toNmsItemStack()
            )
            sendEntityData()
        }

    var itemDisplayContext: ItemDisplayContext = ItemDisplayContext.NONE
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_ItemDisplayAccessor.FIELD_DATA_ITEM_DISPLAY_ID,
                value.getId()
            )
            sendEntityData()
        }

    init {
        item = display
        defineDefaultValues()
    }

    private fun defineDefaultValues() {
        itemDisplayContext = ItemDisplayContext.NONE
    }

}