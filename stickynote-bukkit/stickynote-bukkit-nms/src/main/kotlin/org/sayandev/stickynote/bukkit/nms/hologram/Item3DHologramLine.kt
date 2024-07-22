package org.sayandev.stickynote.bukkit.nms.hologram

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.nms.NMSUtils.sendPacket
import org.sayandev.stickynote.bukkit.nms.NMSUtils.toNmsItemStack
import org.sayandev.stickynote.bukkit.nms.PacketUtils
import org.sayandev.stickynote.bukkit.nms.npc.entity.ItemNPC
import org.sayandev.stickynote.nms.accessors.EntityDataAccessorAccessor
import org.sayandev.stickynote.nms.accessors.ItemEntityAccessor

/**
 * Item3DHologramLine displays a 3D item in a Hologram using an ItemEntity (Like a dropped item)
 * @see Hologram
 */
open class Item3DHologramLine(
    private val item: ItemStack,
    private var glowing: Boolean,
    distance: Float
): ItemHoloLine(distance) {

    /**
     * Gets the item of the hologram line
     * @return ItemStack of the hologram line
     */
    override fun getItem(): ItemStack {
        return getItemNPC().getItem()
    }

    /**
     * Sets the item of the hologram line
     * @param item ItemStack to set
     */
    override fun setItem(item: ItemStack) {
        getItemNPC().setItem(item)
    }

    /**
     * Sets the item of the hologram line for a player only. The hologram line will not be updated for other players
     * @param item ItemStack to set
     * @param player Player to set the item for
     */
    override fun setItem(item: ItemStack, player: Player) {
        player.sendPacket(PacketUtils.getEntityDataPacket(
            npc.entityId,
            EntityDataAccessorAccessor.METHOD_GET_ID!!.invoke(ItemEntityAccessor.FIELD_DATA_ITEM!!) as Int,
            item.toNmsItemStack())
        )
    }

    /**
     * Checks if the item is glowing
     * @return true if the item is glowing
     */
    override fun isGlowing(): Boolean {
        return glowing
    }

    /**
     * Sets the item to glow
     * @param glowing true to make the item glow
     */
    override fun setGlowing(glowing: Boolean) {
        npc.setGlowing(glowing)
    }

    override fun initializeNPC(location: Location) {
        npc = ItemNPC(location, item)
        npc.setNoGravity(true)
        npc.setGlowing(glowing)
    }

    protected fun getItemNPC(): ItemNPC {
        return npc as ItemNPC
    }

}