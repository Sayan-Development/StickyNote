package org.sayandev.stickynote.bukkit.nms.hologram

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.NMSUtils.sendPacket
import org.sayandev.stickynote.bukkit.nms.PacketUtils
import org.sayandev.stickynote.bukkit.nms.npc.entity.ThrowableProjectileNPC
import org.sayandev.stickynote.bukkit.nms.accessors.EntityDataAccessorAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.ThrowableItemProjectileAccessor

/**
 * Item2DHologramLine displays a 2D item in a Hologram using a throwable projectile (Like a snowball)
 * @see Hologram
 */
class Item2DHologramLine(
    private val item: ItemStack,
    private var glowing: Boolean,
    distance: Float
): ItemHoloLine(distance) {

    /**
     * Gets the item of the hologram line
     * @return ItemStack of the hologram line
     */
    override fun getItem(): ItemStack {
        return getThrowableProjectileNPC().getItem()
    }

    /**
     * Sets the item of the hologram line
     * @param item ItemStack to set
     */
    override fun setItem(item: ItemStack) {
        if (isInitialized()) {
            getThrowableProjectileNPC().setItem(item)
        }
    }

    /**
     * Sets the item of the hologram line for a player only. The hologram line will not be updated for other players
     * @param item ItemStack to set
     * @param player Player to set the item for
     */
    override fun setItem(item: ItemStack, player: Player) {
        player.sendPacket(PacketUtils.getEntityDataPacket(
            npc.entityId,
            EntityDataAccessorAccessor.METHOD_GET_ID!!.invoke(ThrowableItemProjectileAccessor.FIELD_DATA_ITEM_STACK!!) as Int,
            NMSUtils.getNmsItemStack(item),
        ))
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
        this.glowing = glowing
        npc.setGlowing(glowing)
    }

    override fun initializeNPC(location: Location) {
        npc = ThrowableProjectileNPC(location, item)
        npc.setNoGravity(true)
        npc.setGlowing(glowing)
    }

    internal fun getThrowableProjectileNPC(): ThrowableProjectileNPC {
        return npc as ThrowableProjectileNPC
    }

}