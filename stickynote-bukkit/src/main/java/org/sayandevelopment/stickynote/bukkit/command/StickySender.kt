package org.sayandevelopment.stickynote.bukkit.command

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.sayandevelopment.stickynote.bukkit.command.interfaces.SenderExtension
import org.sayandevelopment.stickynote.bukkit.utils.AdventureUtils
import org.sayandevelopment.stickynote.bukkit.utils.AdventureUtils.sendMessage

open class StickySender(
    private var commandSender: CommandSender
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

}