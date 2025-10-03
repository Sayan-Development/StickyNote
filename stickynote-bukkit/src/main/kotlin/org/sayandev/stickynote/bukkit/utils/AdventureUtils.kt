package org.sayandev.stickynote.bukkit.utils

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
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
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(sender).sendMessage(PlaceholderAPIHook.injectPlaceholders(sender as? Player, message).component(*placeholder))
        } else {
            sender.sendMessage(PlaceholderAPIHook.injectPlaceholders(sender as? Player, message).component(*placeholder))
        }
    }

    @JvmStatic
    fun sendComponent(sender: CommandSender, message: Component, vararg placeholder: TagResolver) {
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(sender).sendMessage(message)
        } else {
            sender.sendMessage(message)
        }
    }

    @JvmStatic
    fun sendComponent(sender: CommandSender, message: Component) {
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(sender).sendMessage(message)
        } else {
            sender.sendMessage(message)
        }
    }

    @JvmStatic
    fun sendComponentActionbar(player: Player, content: String, vararg placeholder: TagResolver) {
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(player).sendActionBar(PlaceholderAPIHook.injectPlaceholders(player, content).component(*placeholder))
        } else {
            player.sendActionBar(PlaceholderAPIHook.injectPlaceholders(player, content).component(*placeholder))
        }
    }

    @JvmStatic
    fun sendComponentActionbar(player: Player, content: Component, vararg placeholder: TagResolver) {
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(player).sendActionBar(content)
        } else {
            player.sendActionBar(content)
        }
    }

    @JvmStatic
    fun sendComponentActionbar(sender: CommandSender, content: Component) {
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(sender).sendActionBar(content)
        } else {
            sender.sendActionBar(content)
        }
    }

    @JvmStatic
    fun openBook(
        sender: CommandSender,
        title: Component,
        author: Component,
        vararg pages: Component
    ) {
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(sender).openBook(Book.book(
                title,
                author,
                *pages
            ))
        } else {
            sender.openBook(
                Book.book(
                    title,
                    author,
                    *pages
                )
            )
        }
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