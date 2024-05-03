package org.sayandev.stickynote.bukkit.item.extension

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface ItemExtension {

    val id: String

    fun lore(item: ItemStack, player: Player?): List<Component>

    fun serialize(): String

}