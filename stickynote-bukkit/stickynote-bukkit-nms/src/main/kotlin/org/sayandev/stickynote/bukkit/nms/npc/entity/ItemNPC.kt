package org.sayandev.stickynote.bukkit.nms.npc.entity

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.NMSUtils.toNmsItemStack
import org.sayandev.stickynote.bukkit.nms.npc.EntityNPC
import org.sayandev.stickynote.bukkit.nms.npc.NPCType
import org.sayandev.stickynote.nms.accessors.ItemEntityAccessor

class ItemNPC(
    location: Location,
    item: ItemStack
): EntityNPC(
    ItemEntityAccessor.CONSTRUCTOR_0!!.newInstance(NMSUtils.getServerLevel(location.world), location.x, location.y, location.z, item.toNmsItemStack()),
    location,
    NPCType.ITEM
) {

    fun setItem(item: ItemStack) {
        ItemEntityAccessor.METHOD_SET_ITEM!!.invoke(entity, item.toNmsItemStack())
        sendEntityData()
    }

    fun getItem(): ItemStack {
        return NMSUtils.getBukkitItemStack(ItemEntityAccessor.METHOD_GET_ITEM!!.invoke(entity))
    }

    fun setAmount(amount: Int) {
        val item = getItem()
        item.amount = amount
        setItem(item)
    }

    fun getAmount(): Int {
        return getItem().amount
    }

    fun collect(collectorEntityId: Int) {
        super.collect(entityId, collectorEntityId, getAmount())
    }

}