package org.sayandev.stickynote.bukkit.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.extension.sendComponent
import org.sayandev.stickynote.core.command.interfaces.SenderExtension

open class BukkitSender(
    private var commandSender: CommandSender,
    val sourceStack: CommandSourceStack?
): SenderExtension<CommandSender, Player> {

    private var onlinePlayersMessage: String = "<red>Only players can use this command."

    override fun player(): Player? {
        if (commandSender is Player) return (commandSender as Player).player

        if (onlinePlayersMessage.isNotEmpty()) {
            commandSender.sendComponent(onlinePlayersMessage)
        }

        return null
    }

    override fun platformSender(): CommandSender {
        return commandSender
    }

    override fun platformSender(sender: CommandSender) {
        commandSender = sender
    }

    override fun onlyPlayersComponent(message: String) {
        onlinePlayersMessage = message
    }

}