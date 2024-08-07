package org.sayandev.stickynote.bukkit.utils

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.hook.PlaceholderAPIHook
import org.sayandev.stickynote.bukkit.plugin

object AdventureUtils {

    @JvmStatic
    val audience = BukkitAudiences.create(plugin)

    @JvmStatic
    val miniMessage = MiniMessage.miniMessage()

    fun CommandSender.sendComponent(message: String, vararg placeholder: TagResolver) {
        audience.sender(this).sendMessage(PlaceholderAPIHook.injectPlaceholders(this as? Player, message).component(*placeholder))
    }

    fun Player.sendComponentActionbar(content: String, vararg placeholder: TagResolver) {
        audience.player(this).sendActionBar(PlaceholderAPIHook.injectPlaceholders(this as? Player, content).component(*placeholder))
    }

    fun toComponent(content: String, vararg placeholder: TagResolver): Component {
        return miniMessage.deserialize(content, *placeholder)
    }

    fun toComponent(player: Player?, content: String, vararg placeholder: TagResolver): Component {
        return miniMessage.deserialize(PlaceholderAPIHook.injectPlaceholders(player, content), *placeholder)
    }

    fun String.component(vararg placeholder: TagResolver): Component {
        return toComponent(this, *placeholder)
    }

    fun String.component(player: Player?, vararg placeholder: TagResolver): Component {
        return toComponent(player, this, *placeholder)
    }
}