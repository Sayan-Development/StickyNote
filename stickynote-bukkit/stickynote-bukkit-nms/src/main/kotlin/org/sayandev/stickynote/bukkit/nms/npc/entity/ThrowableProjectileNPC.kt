package org.sayandev.stickynote.bukkit.nms.npc.entity

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.NMSUtils.toNmsItemStack
import org.sayandev.stickynote.bukkit.nms.accessors.AbstractThrownPotionAccessor
import org.sayandev.stickynote.bukkit.nms.npc.EntityNPC
import org.sayandev.stickynote.bukkit.nms.npc.NPCType
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.bukkit.nms.accessors.SynchedEntityDataAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.ThrowableItemProjectileAccessor

class ThrowableProjectileNPC(
    location: Location,
    item: ItemStack
): EntityNPC(
    if (ServerVersion.supports(14)) AbstractThrownPotionAccessor.CONSTRUCTOR_0!!.newInstance(NPCType.POTION.nmsEntityType(), NMSUtils.getServerLevel(location.world))
    else AbstractThrownPotionAccessor.CONSTRUCTOR_1!!.newInstance(NMSUtils.getServerLevel(location.world)),
    location,
    NPCType.POTION
) {

    init {
        setItem(item)
    }

    /**
     * Sets the item of the NPC
     * @param item ItemStack to set
     */
    fun setItem(item: ItemStack) {
        val nmsItem = item.toNmsItemStack()
        if (ServerVersion.supports(16)) {
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                ThrowableItemProjectileAccessor.FIELD_DATA_ITEM_STACK!!,
                nmsItem
            )
        } else {
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(getEntityData(), AbstractThrownPotionAccessor.FIELD_DATA_ITEM_STACK!!, nmsItem)
        }
        sendEntityData()
    }

    /**
     * Gets the item of the NPC
     * @return ItemStack of the NPC
     */
    fun getItem(): ItemStack {
        return NMSUtils.getBukkitItemStack(
            (if (ServerVersion.supports(16)) ThrowableItemProjectileAccessor.METHOD_GET_ITEM_RAW!!.invoke(entity)
            else SynchedEntityDataAccessor.METHOD_GET!!.invoke(getEntityData(), AbstractThrownPotionAccessor.FIELD_DATA_ITEM_STACK!!))
        )
    }

}