package org.sayandev.stickynote.bukkit.utils

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.StickyNote
import org.sayandev.stickynote.bukkit.hook.PlaceholderAPIHook
import org.sayandev.stickynote.bukkit.plugin

object AdventureUtils {

    private var options = Options.defaultOptions()

    @JvmStatic
    fun setOptions(options: Options) {
        this.options = options
    }

    @JvmStatic
    val audience = BukkitAudiences.create(plugin)

    @JvmStatic
    var miniMessage = MiniMessage.miniMessage()
    @JvmStatic
    var legacyAmpersandSerializer = LegacyComponentSerializer.legacyAmpersand()

    fun senderAudience(sender: CommandSender): Audience {
        return if (StickyNote.isPaper && ServerVersion.supports(18)) {
            sender
        } else {
            audience.sender(sender)
        }
    }

    fun setTagResolver(vararg tagResolver: TagResolver) {
        miniMessage = MiniMessage.builder().tags(TagResolver.resolver(TagResolver.standard(), *tagResolver)).build()
    }

    @JvmStatic
    fun sendComponent(sender: CommandSender, message: String, vararg placeholder: TagResolver) {
        senderAudience(sender).sendMessage(PlaceholderAPIHook.injectPlaceholders(sender as? Player, message).component(*placeholder))
    }

    @JvmStatic
    fun sendComponentActionbar(player: Player, content: String, vararg placeholder: TagResolver) {
        senderAudience(player).sendActionBar(PlaceholderAPIHook.injectPlaceholders(player, content).component(*placeholder))
    }

    @JvmStatic
    fun toComponent(content: String, vararg placeholder: TagResolver): Component {
        val component = miniMessage.deserialize(content, *placeholder)
        return if (options.removeStartingItalic) Component.empty().decoration(TextDecoration.ITALIC, false).append(component) else component
    }

    @JvmStatic
    fun toComponent(player: Player?, content: String, vararg placeholder: TagResolver): Component {
        return miniMessage.deserialize(PlaceholderAPIHook.injectPlaceholders(player, content), *placeholder)
    }

    fun String.component(vararg placeholder: TagResolver): Component {
        return toComponent(this, *placeholder)
    }

    fun String.component(player: Player?, vararg placeholder: TagResolver): Component {
        return toComponent(player, this, *placeholder)
    }

    fun Component.legacyString(): String {
        return legacyAmpersandSerializer.serialize(this)
    }

    fun String.legacyColored(): String {
        return ChatColor.translateAlternateColorCodes('&', this)
    }

    fun Component.legacyColored(): String {
        return this.legacyString().legacyColored()
    }

    data class Options(
        val removeStartingItalic: Boolean = true
    ) {
        companion object {
            fun defaultOptions(): Options {
                return Options()
            }
        }
    }
}