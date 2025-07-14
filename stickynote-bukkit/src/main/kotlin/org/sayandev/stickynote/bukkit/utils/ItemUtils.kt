package org.sayandev.stickynote.bukkit.utils

import net.kyori.adventure.text.Component
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.StickyNote
import org.sayandev.stickynote.bukkit.utils.AdventureUtils.bungeeComponent
import org.sayandev.stickynote.bukkit.utils.AdventureUtils.component
import org.sayandev.stickynote.bukkit.utils.AdventureUtils.legacyColored

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

    fun ItemStack.withDisplayName(displayName: Component): ItemStack {
        if (StickyNote.isPaper && ServerVersion.supports(18)) {
            this.editMeta {
                it.displayName(displayName)
            }
        } else {
            if (ServerVersion.supports(16)) {
                this.itemMeta.let { meta ->
                    meta.setDisplayNameComponent(displayName.bungeeComponent())
                    this.itemMeta = meta
                }
            } else {
                this.itemMeta.let { meta ->
                    meta.setDisplayName(displayName.legacyColored())
                    this.itemMeta = meta
                }
            }
        }
        return this
    }

    fun ItemStack.withLore(lore: List<Component>): ItemStack {
        if (StickyNote.isPaper && ServerVersion.supports(18)) {
            this.editMeta {
                it.lore(lore)
            }
        } else {
            if (ServerVersion.supports(16)) {
                this.itemMeta.let { meta ->
                    meta.loreComponents = lore.map { it.bungeeComponent() }
                    this.itemMeta = meta
                }
            } else {
                this.itemMeta.let { meta ->
                    meta.lore = lore.map { it.legacyColored() }
                    this.itemMeta = meta
                }
            }
        }
        return this
    }
}