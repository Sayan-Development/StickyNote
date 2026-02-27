package org.sayandev.stickynote.paper.nms.npc.entity

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.paper.nms.NMSUtils
import org.sayandev.stickynote.paper.nms.NMSUtils.toNmsItemStack
import org.sayandev.stickynote.paper.nms.npc.EntityNPC
import org.sayandev.stickynote.paper.nms.npc.NPCType
import org.sayandev.stickynote.paper.nms.accessors.ItemEntityAccessor

class ItemNPC(
    location: Location,
    item: ItemStack
): EntityNPC(
    ItemEntityAccessor.CONSTRUCTOR_0!!.newInstance(NMSUtils.getServerLevel(location.world), location.x, location.y, location.z, item.toNmsItemStack()),
    location,
    NPCType.ITEM
) {

    /**
     * Sets the item of the NPC
     * @param item ItemStack to set
     */
    fun setItem(item: ItemStack) {
        ItemEntityAccessor.METHOD_SET_ITEM!!.invoke(entity, item.toNmsItemStack())
        sendEntityData()
    }

    /**
     * Gets the item of the NPC
     * @return ItemStack of the NPC
     */
    fun getItem(): ItemStack {
        return NMSUtils.getBukkitItemStack(ItemEntityAccessor.METHOD_GET_ITEM!!.invoke(entity))
    }

    /**
     * Sets the amount of the item
     * @param amount Amount to set
     */
    fun setAmount(amount: Int) {
        val item = getItem()
        item.amount = amount
        setItem(item)
    }

    /**
     * Gets the amount of the item
     * @return Amount of the item
     */
    fun getAmount(): Int {
        return getItem().amount
    }

    /**
     * Plays a collect item animation for this item. It is recommended to discard the NPC after calling this method
     * @param collectorEntityId The entity id of the entity that will collect the item (Doesn't actually collect, it just plays the animation)
     */
    fun collect(collectorEntityId: Int) {
        super.collect(entityId, collectorEntityId, getAmount())
    }

}