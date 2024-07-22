package org.sayandev.stickynote.bukkit.nms.hologram

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class ItemHoloLine(
    distance: Float
): HoloLine(distance) {

    abstract fun getItem(): ItemStack

    abstract fun setItem(item: ItemStack)

    abstract fun setItem(item: ItemStack, player: Player)

    abstract fun isGlowing(): Boolean

    abstract fun setGlowing(glowing: Boolean)

}