package org.sayandev.stickynote.bukkit.extension

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.sayandev.sayanventure.adventure.text.minimessage.tag.resolver.TagResolver
import org.sayandev.stickynote.bukkit.utils.AdventureUtils

fun CommandSender.sendComponent(message: String, vararg placeholder: TagResolver) {
    AdventureUtils.sendComponent(this, message, *placeholder)
}

fun Player.sendComponentActionbar(content: String, vararg placeholder: TagResolver) {
    AdventureUtils.sendComponentActionbar(this, content, *placeholder)
}