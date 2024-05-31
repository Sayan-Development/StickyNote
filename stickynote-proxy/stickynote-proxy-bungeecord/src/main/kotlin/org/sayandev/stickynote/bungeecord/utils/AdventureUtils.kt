package org.sayandev.stickynote.bungeecord.utils

import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.sayandev.stickynote.bungeecord.plugin

object AdventureUtils {

    @JvmStatic
    val audience = BungeeAudiences.create(plugin)

    @JvmStatic
    val miniMessage = MiniMessage.miniMessage()

    @JvmStatic
    fun CommandSender.sendMessage(message: Component) {
        audience.sender(this).sendMessage(message)
    }

    @JvmStatic
    fun ProxiedPlayer.sendActionbar(content: Component) {
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