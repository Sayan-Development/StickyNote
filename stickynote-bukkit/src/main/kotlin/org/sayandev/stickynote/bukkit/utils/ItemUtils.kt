package org.sayandev.stickynote.bukkit.utils

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.StickyNote
import org.sayandev.stickynote.bukkit.utils.AdventureUtils.adventureComponent
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

    fun ItemStack.withDisplayName(displayName: String, placeholders: Map<String, String> = emptyMap()): ItemStack {
        if (StickyNote.isPaper && ServerVersion.supports(18)) {
            this.editMeta {
                it.displayName(displayName.adventureComponent(*placeholders.map { Placeholder.parsed(it.key, it.value) }.toTypedArray()))
            }
        } else {
            if (ServerVersion.supports(16)) {
                this.itemMeta.let { meta ->
                    meta.setDisplayNameComponent(displayName.component(*placeholders.map { org.sayandev.sayanventure.adventure.text.minimessage.tag.resolver.Placeholder.parsed(it.key, it.value) }.toTypedArray()).bungeeComponent())
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

    fun ItemStack.withLore(lore: List<String>, placeholders: Map<String, String> = emptyMap()): ItemStack {
        if (StickyNote.isPaper && ServerVersion.supports(18)) {
            this.editMeta {
                it.lore(lore.map {
                    it.adventureComponent(*placeholders.map { Placeholder.parsed(it.key, it.value) }.toTypedArray())
                })
            }
        } else {
            if (ServerVersion.supports(16)) {
                this.itemMeta.let { meta ->
                    meta.loreComponents = lore.map { it.component(*placeholders.map { org.sayandev.sayanventure.adventure.text.minimessage.tag.resolver.Placeholder.parsed(it.key, it.value) }.toTypedArray()).bungeeComponent() }
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