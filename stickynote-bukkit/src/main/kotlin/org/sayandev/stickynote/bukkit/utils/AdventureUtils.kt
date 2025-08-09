package org.sayandev.stickynote.bukkit.utils

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.sayandev.sayanventure.adventure.audience.Audience
import org.sayandev.sayanventure.adventure.inventory.Book
import org.sayandev.sayanventure.adventure.platform.bukkit.BukkitAudiences
import org.sayandev.sayanventure.adventure.text.Component
import org.sayandev.sayanventure.adventure.text.format.TextDecoration
import org.sayandev.sayanventure.adventure.text.minimessage.MiniMessage
import org.sayandev.sayanventure.adventure.text.minimessage.tag.resolver.TagResolver
import org.sayandev.sayanventure.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import org.sayandev.sayanventure.adventure.text.serializer.gson.GsonComponentSerializer
import org.sayandev.sayanventure.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.sayandev.stickynote.bukkit.StickyNote
import org.sayandev.stickynote.bukkit.hook.PlaceholderAPIHook
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.core.component.StickyComponent
import org.sayandev.stickynote.core.component.StickyTag
import org.sayandev.stickynote.core.component.modes.StickyComponentMiniMessage

object AdventureUtils {

    private var options = Options.defaultOptions()

    @JvmStatic
    fun setOptions(options: Options) {
        this.options = options
    }

    @JvmStatic
    val audience = BukkitAudiences.create(plugin)

    @JvmStatic
    @Deprecated("use sticky minimessage instead")
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
            sender.sendMessage(PlaceholderAPIHook.injectPlaceholders(sender as? Player, message).component(*placeholder).adventureComponent())
        }
    }

    @JvmStatic
    fun sendComponent(sender: CommandSender, message: Component) {
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(sender).sendMessage(message)
        } else {
            sender.sendMessage(message.adventureComponent())
        }
    }

    @JvmStatic
    fun sendComponentActionbar(player: Player, content: String, vararg placeholder: TagResolver) {
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(player).sendActionBar(PlaceholderAPIHook.injectPlaceholders(player, content).component(*placeholder))
        } else {
            player.sendActionBar(PlaceholderAPIHook.injectPlaceholders(player, content).component(*placeholder).adventureComponent())
        }
    }

    @JvmStatic
    fun sendComponentActionbar(sender: CommandSender, content: Component) {
        if (!ServerVersion.isAtLeast(21, 6)) {
            senderAudience(sender).sendActionBar(content)
        } else {
            sender.sendActionBar(content.adventureComponent())
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
                net.kyori.adventure.inventory.Book.book(
                    title.adventureComponent(),
                    author.adventureComponent(),
                    *pages.map { it.adventureComponent() }.toTypedArray()
                )
            )
        }
    }

    @JvmStatic
    fun toComponent(content: String, vararg placeholder: StickyTag): StickyComponent {
        val component = StickyComponentMiniMessage(content, placeholder.toList())
        return if (options.removeStartingItalic) Component.empty().decoration(TextDecoration.ITALIC, false).append(component) else component
    }

    @JvmStatic
    fun toComponent(player: Player?, content: String, vararg placeholder: TagResolver): StickyComponent {
        return miniMessage.deserialize(PlaceholderAPIHook.injectPlaceholders(player, content), *placeholder)
    }

    fun String.component(vararg placeholder: StickyTag): StickyComponent {
        return toComponent(this, *placeholder)
    }

    fun String.component(player: Player?, vararg placeholder: StickyTag): StickyComponent {
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