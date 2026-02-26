package org.sayandev.stickynote.command.bukkit

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.IStringTooltip
import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.command.CommandSender
import org.sayandev.stickynote.bukkit.launch
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.command.CommandExtension

abstract class BukkitCommand(
    private val name: String,
    private vararg val aliases: String,
) : CommandExtension {

    init {
        register()
    }

    final override fun register() {
        val command = CommandAPICommand(name)
            .withAliases(*aliases)

        build(command)
        command.register(plugin)
    }

    final override fun unregister() {
        CommandAPI.unregister(name, true)
        aliases.forEach {
            CommandAPI.unregister(it, true)
        }
    }

    protected abstract fun build(command: CommandAPICommand)
}

fun CommandAPICommand.executesSuspending(handler: suspend (CommandSender, CommandArguments) -> Unit): CommandAPICommand {
    return executes(CommandExecutor { sender, arguments ->
        launch {
            handler(sender, arguments)
        }
    })
}

fun CommandAPICommand.executesCommand(handler: (CommandSender, CommandArguments) -> Unit): CommandAPICommand {
    return executes(CommandExecutor { sender, arguments ->
        handler(sender, arguments)
    })
}

fun StringArgument.suggest(values: Collection<String>): StringArgument {
    return replaceSuggestions(ArgumentSuggestions.strings(values)) as StringArgument
}

fun StringArgument.suggest(provider: (SuggestionInfo<CommandSender>) -> Collection<String>): StringArgument {
    return replaceSuggestions(ArgumentSuggestions.stringCollection(provider)) as StringArgument
}

fun StringArgument.suggestTooltip(values: Collection<IStringTooltip>): StringArgument {
    return replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(values)) as StringArgument
}

fun StringArgument.suggestTooltip(provider: (SuggestionInfo<CommandSender>) -> Collection<IStringTooltip>): StringArgument {
    return replaceSuggestions(ArgumentSuggestions.stringsWithTooltipsCollection(provider)) as StringArgument
}

fun GreedyStringArgument.suggest(values: Collection<String>): GreedyStringArgument {
    return replaceSuggestions(ArgumentSuggestions.strings(values)) as GreedyStringArgument
}

fun GreedyStringArgument.suggest(provider: (SuggestionInfo<CommandSender>) -> Collection<String>): GreedyStringArgument {
    return replaceSuggestions(ArgumentSuggestions.stringCollection(provider)) as GreedyStringArgument
}

fun <T, B> CustomArgument<T, B>.suggest(values: Collection<String>): CustomArgument<T, B> {
    replaceSuggestions(ArgumentSuggestions.strings(values))
    return this
}

fun <T, B> CustomArgument<T, B>.suggest(provider: (SuggestionInfo<CommandSender>) -> Collection<String>): CustomArgument<T, B> {
    replaceSuggestions(ArgumentSuggestions.stringCollection(provider))
    return this
}

fun <T, B> CustomArgument<T, B>.suggestTooltip(values: Collection<IStringTooltip>): CustomArgument<T, B> {
    replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(values))
    return this
}

fun <T, B> CustomArgument<T, B>.suggestTooltip(provider: (SuggestionInfo<CommandSender>) -> Collection<IStringTooltip>): CustomArgument<T, B> {
    replaceSuggestions(ArgumentSuggestions.stringsWithTooltipsCollection(provider))
    return this
}
