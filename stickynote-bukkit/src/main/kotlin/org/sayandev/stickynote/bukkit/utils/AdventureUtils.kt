package org.sayandev.stickynote.bukkit.utils

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.plugin

object AdventureUtils {

    @JvmStatic
    val audience = BukkitAudiences.create(plugin)

    @JvmStatic
    val miniMessage = MiniMessage.miniMessage()

    @JvmStatic
    fun CommandSender.sendMessage(message: Component) {
        audience.sender(this).sendMessage(message)
    }

    @JvmStatic
    fun Player.sendActionbar(content: Component) {
        audience.player(this).sendActionBar(content)
    }

    @JvmStatic
    fun toComponent(content: String, vararg placeholder: TagResolver): Component {
        return miniMessage.deserialize(content, *placeholder)
    }

    fun String.component(vararg placeholder: TagResolver): Component {
        return toComponent(this, *placeholder)
    }
}