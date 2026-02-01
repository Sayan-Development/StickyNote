package org.sayandev.stickynote.bukkit.extension

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.sayandev.stickynote.bukkit.utils.AdventureUtils

fun CommandSender.sendComponent(message: String, vararg placeholder: TagResolver) {
    AdventureUtils.sendComponent(this, message, *placeholder)
}

fun CommandSender.sendComponent(message: Component, vararg placeholder: TagResolver) {
    AdventureUtils.sendComponent(this, message, *placeholder)
}

fun Player.sendComponentActionbar(content: String, vararg placeholder: TagResolver) {
    AdventureUtils.sendComponentActionbar(this, content, *placeholder)
}

fun Player.sendComponentActionbar(content: Component, vararg placeholder: TagResolver) {
    AdventureUtils.sendComponentActionbar(this, content, *placeholder)
}

fun CommandSender.openBook(title: Component, author: Component, vararg pages: Component) {
    AdventureUtils.openBook(this, title, author, *pages)
}