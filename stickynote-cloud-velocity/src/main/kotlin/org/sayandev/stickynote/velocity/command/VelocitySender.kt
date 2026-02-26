package org.sayandev.stickynote.velocity.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import org.sayandev.stickynote.core.command.interfaces.SenderExtension
import org.sayandev.stickynote.velocity.utils.AdventureUtils.component

open class VelocitySender(
    private var source: CommandSource,
): SenderExtension<CommandSource, Player> {

    private var onlinePlayersMessage: String = "<red>Only players can use this command."

    override fun player(): Player? {
        if (source is Player) return (source as Player)

        if (onlinePlayersMessage.isNotEmpty()) {
            source.sendMessage(onlinePlayersMessage.component())
        }

        return null
    }

    override fun platformSender(): CommandSource {
        return source
    }

    override fun platformSender(sender: CommandSource) {
        source = sender
    }

    override fun onlyPlayersComponent(message: String) {
        onlinePlayersMessage = message
    }

}