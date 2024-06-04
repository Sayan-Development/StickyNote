package org.sayandev.stickynote.bukkit.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.command.interfaces.SenderExtension
import org.sayandev.stickynote.bukkit.utils.AdventureUtils

open class StickySender(
    private var commandSender: CommandSender,
    val sourceStack: CommandSourceStack?
): SenderExtension {

    private var onlinePlayersMessage = Component.text("Only players can use this command.")
        .color(TextColor.color(192, 32, 16))
        .asComponent()

    override fun player(): Player? {
        if (commandSender is Player) return (commandSender as Player).player

        if (onlinePlayersMessage != Component.empty()) {
            commandSender.sendMessage(onlinePlayersMessage)
        }

        return null
    }

    override fun audience(): Audience {
        return AdventureUtils.audience.sender(commandSender)
    }

    override fun bukkitSender(sender: CommandSender) {
        commandSender = sender
    }

    override fun bukkitSender(): CommandSender {
        return commandSender
    }

    override fun onlyPlayersComponent(message: Component) {
        onlinePlayersMessage = message
    }

    override fun getLocation(): Location {
        return sourceStack?.location ?: executor?.location!!
    }

    override fun getSender(): CommandSender {
        return sourceStack?.sender ?: commandSender
    }

    override fun getExecutor(): Entity? {
        return sourceStack?.executor ?: player()
    }

}