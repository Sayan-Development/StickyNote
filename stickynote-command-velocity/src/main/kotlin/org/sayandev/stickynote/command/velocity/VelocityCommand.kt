package org.sayandev.stickynote.command.velocity

import com.velocitypowered.api.command.CommandSource
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import org.sayandev.stickynote.command.CommandExtension
import org.sayandev.stickynote.velocity.launch
import org.sayandev.stickynote.velocity.plugin

abstract class VelocityCommand(
    private val name: String,
    private vararg val aliases: String,
) : CommandExtension {

    final override fun register() {
        val command = CommandAPICommand(name)
            .withAliases(*aliases)

        build(command)
        command.register(plugin.instance)
    }

    final override fun unregister() {
        CommandAPI.unregister(name, true)
        aliases.forEach {
            CommandAPI.unregister(it, true)
        }
    }

    protected abstract fun build(command: CommandAPICommand)
}

fun CommandAPICommand.executesCommand(handler: (CommandSource, CommandArguments) -> Unit): CommandAPICommand {
    return executes(CommandExecutor { sender, arguments ->
        handler(sender, arguments)
    })
}

fun CommandAPICommand.executesSuspending(handler: suspend (CommandSource, CommandArguments) -> Unit): CommandAPICommand {
    return executes(CommandExecutor { sender, arguments ->
        launch {
            handler(sender, arguments)
        }
    })
}

fun StringArgument.suggest(values: Collection<String>): StringArgument {
    return replaceSuggestions(ArgumentSuggestions.strings(values)) as StringArgument
}

fun StringArgument.suggest(provider: (SuggestionInfo<CommandSource>) -> Collection<String>): StringArgument {
    return replaceSuggestions(ArgumentSuggestions.stringCollection(provider)) as StringArgument
}
