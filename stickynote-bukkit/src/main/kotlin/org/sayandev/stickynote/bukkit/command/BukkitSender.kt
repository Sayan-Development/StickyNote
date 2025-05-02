package org.sayandev.stickynote.bukkit.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.sayandev.stickynote.core.command.interfaces.SenderExtension
import org.sayandev.stickynote.bukkit.utils.AdventureUtils

open class BukkitSender(
    private var commandSender: CommandSender,
    val sourceStack: CommandSourceStack?
): SenderExtension<CommandSender, Player> {

    private var onlinePlayersMessage: Component = Component.text("Only players can use this command.")
        .color(TextColor.color(192, 32, 16))

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

    override fun platformSender(): CommandSender {
        return commandSender
    }

    override fun platformSender(sender: CommandSender) {
        commandSender = sender
    }

    override fun onlyPlayersComponent(message: Component) {
        onlinePlayersMessage = message
    }

}