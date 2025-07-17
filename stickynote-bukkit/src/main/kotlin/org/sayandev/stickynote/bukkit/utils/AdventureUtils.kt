package org.sayandev.stickynote.bukkit.utils

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.sayandev.sayanventure.adventure.audience.Audience
import org.sayandev.sayanventure.adventure.platform.bukkit.BukkitAudiences
import org.sayandev.sayanventure.adventure.text.Component
import org.sayandev.sayanventure.adventure.text.format.TextDecoration
import org.sayandev.sayanventure.adventure.text.minimessage.MiniMessage
import org.sayandev.sayanventure.adventure.text.minimessage.tag.resolver.TagResolver
import org.sayandev.sayanventure.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import org.sayandev.sayanventure.adventure.text.serializer.legacy.LegacyComponentSerializer
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
    @JvmStatic
    var bungeeComponentSerializer = BungeeComponentSerializer.get()

    fun senderAudience(sender: CommandSender): Audience {
        return audience.sender(sender)
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
    fun toAdventureComponent(content: String, vararg placeholder: net.kyori.adventure.text.minimessage.tag.resolver.TagResolver): net.kyori.adventure.text.Component {
        val component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(content, *placeholder)
        return if (options.removeStartingItalic) net.kyori.adventure.text.Component.empty().decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false).append(component) else component
    }

    @JvmStatic
    fun toComponent(player: Player?, content: String, vararg placeholder: TagResolver): Component {
        return miniMessage.deserialize(PlaceholderAPIHook.injectPlaceholders(player, content), *placeholder)
    }

    @JvmStatic
    fun toAdventureComponent(player: Player?, content: String, vararg placeholder: net.kyori.adventure.text.minimessage.tag.resolver.TagResolver): net.kyori.adventure.text.Component {
        return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(PlaceholderAPIHook.injectPlaceholders(player, content), *placeholder)
    }

    fun String.component(vararg placeholder: TagResolver): Component {
        return toComponent(this, *placeholder)
    }

    fun String.adventureComponent(vararg placeholder: net.kyori.adventure.text.minimessage.tag.resolver.TagResolver): net.kyori.adventure.text.Component {
        return toAdventureComponent(this, *placeholder)
    }

    fun String.component(player: Player?, vararg placeholder: TagResolver): Component {
        return toComponent(player, this, *placeholder)
    }

    fun String.adventureComponent(player: Player?, vararg placeholder: net.kyori.adventure.text.minimessage.tag.resolver.TagResolver): net.kyori.adventure.text.Component {
        return toAdventureComponent(player, this, *placeholder)
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

    fun Component.bungeeComponent(): Array<BaseComponent> {
        return bungeeComponentSerializer.serialize(this)
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