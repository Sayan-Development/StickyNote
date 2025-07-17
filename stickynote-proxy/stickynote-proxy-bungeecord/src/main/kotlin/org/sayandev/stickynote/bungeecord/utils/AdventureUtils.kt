package org.sayandev.stickynote.bungeecord.utils

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.sayandev.sayanventure.adventure.platform.bungeecord.BungeeAudiences
import org.sayandev.sayanventure.adventure.text.Component
import org.sayandev.sayanventure.adventure.text.minimessage.MiniMessage
import org.sayandev.sayanventure.adventure.text.minimessage.tag.resolver.TagResolver
import org.sayandev.stickynote.bungeecord.plugin

object AdventureUtils {

    @JvmStatic
    val audience = BungeeAudiences.create(plugin)

    @JvmStatic
    var miniMessage = MiniMessage.miniMessage()

    fun setTagResolver(vararg tagResolver: TagResolver) {
        miniMessage = MiniMessage.builder().tags(TagResolver.resolver(TagResolver.standard(), *tagResolver)).build()
    }

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