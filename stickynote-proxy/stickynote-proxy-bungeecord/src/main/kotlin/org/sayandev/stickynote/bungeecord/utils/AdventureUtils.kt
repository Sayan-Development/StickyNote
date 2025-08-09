package org.sayandev.stickynote.bungeecord.utils

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.sayandev.sayanventure.adventure.platform.bungeecord.BungeeAudiences
import org.sayandev.sayanventure.adventure.text.Component
import org.sayandev.sayanventure.adventure.text.minimessage.MiniMessage
import org.sayandev.sayanventure.adventure.text.minimessage.tag.resolver.TagResolver
import org.sayandev.stickynote.bungeecord.plugin
import org.sayandev.stickynote.core.component.StickyComponent
import org.sayandev.stickynote.core.component.StickyTag
import org.sayandev.stickynote.core.component.modes.StickyComponentMiniMessage

object AdventureUtils {

    @JvmStatic
    @Deprecated("use sticky audience instead")
    val audience = BungeeAudiences.create(plugin)

    @JvmStatic
    @Deprecated("use sticky minimessae instead")
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
    fun toComponent(content: String, vararg placeholder: StickyTag): StickyComponent {
        return StickyComponentMiniMessage(content, placeholder.toList())
    }

    fun String.component(vararg placeholder: StickyTag): StickyComponent {
        return toComponent(this, *placeholder)
    }
}