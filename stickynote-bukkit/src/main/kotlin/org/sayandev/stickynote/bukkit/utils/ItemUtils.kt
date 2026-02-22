package org.sayandev.stickynote.bukkit.utils

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.StickyNote
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

    fun ItemStack.withDisplayName(displayName: String, placeholders: Map<String, String> = emptyMap()): ItemStack {
        this.editMeta {
            it.displayName(displayName.component(*placeholders.map { Placeholder.parsed(it.key, it.value) }.toTypedArray()))
        }
        return this
    }

    fun ItemStack.withLore(lore: List<String>, placeholders: Map<String, String> = emptyMap()): ItemStack {
        this.editMeta {
            it.lore(lore.map {
                it.component(*placeholders.map { Placeholder.parsed(it.key, it.value) }.toTypedArray())
            })
        }
        return this
    }
}