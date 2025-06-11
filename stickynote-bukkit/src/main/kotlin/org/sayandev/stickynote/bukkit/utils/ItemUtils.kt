package org.sayandev.stickynote.bukkit.utils

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ItemUtils {
    fun Inventory.hasSpace(item: ItemStack, amount: Int = item.amount): Boolean {
        var remaining = amount
        for (i in 0 until size) {
            val currentItem = getItem(i) ?: continue
            if (currentItem.isSimilar(item)) {
                remaining -= currentItem.maxStackSize - currentItem.amount
                if (remaining <= 0) return true
            }
        }
        return remaining <= 0
    }
}