package org.sayandev.stickynote.velocity.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import org.sayandev.stickynote.core.command.interfaces.SenderExtension

open class VelocitySender(
    private var source: CommandSource,
): SenderExtension<CommandSource, Player> {

    private var onlinePlayersMessage = Component.text("Only players can use this command.")
        .color(TextColor.color(192, 32, 16))
        .asComponent()

    override fun player(): Player? {
        if (source is Player) return (source as Player)

        if (onlinePlayersMessage != Component.empty()) {
            source.sendMessage(onlinePlayersMessage)
        }

        return null
    }

    override fun audience(): Audience {
        return source
    }

    override fun platformSender(): CommandSource {
        return source
    }

    override fun platformSender(sender: CommandSource) {
        source = sender
    }

    override fun onlyPlayersComponent(message: TextComponent) {
        onlinePlayersMessage = message
    }

}