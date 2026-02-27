package org.sayandev.stickynote.command.paper

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.executors.CommandArguments
import org.bukkit.command.CommandSender
import org.sayandev.stickynote.paper.plugin

class CommandDsl internal constructor(private val command: CommandAPICommand) {

    fun permission(permission: String) {
        command.withPermission(permission)
    }

    fun arguments(vararg arguments: Argument<*>) {
        command.withArguments(*arguments)
    }

    fun optionalArguments(vararg arguments: Argument<*>) {
        command.withOptionalArguments(*arguments)
    }

    fun executes(handler: (CommandSender, CommandArguments) -> Unit) {
        command.executesCommand(handler)
    }

    fun executesSuspend(handler: suspend (CommandSender, CommandArguments) -> Unit) {
        command.executesSuspending(handler)
    }

    fun subcommand(name: String, permission: String? = null, build: CommandDsl.() -> Unit = {}) {
        val subcommand = CommandAPICommand(name).dsl {
            permission?.let(::permission)
            build()
        }
        command.withSubcommand(subcommand)
    }

    fun subcommand(subcommand: CommandAPICommand) {
        command.withSubcommand(subcommand)
    }
}

fun CommandAPICommand.dsl(build: CommandDsl.() -> Unit): CommandAPICommand {
    CommandDsl(this).build()
    return this
}

fun command(name: String, vararg aliases: String, build: CommandDsl.() -> Unit): CommandAPICommand {
    return CommandAPICommand(name)
        .withAliases(*aliases)
        .dsl(build)
}

fun commandsUsePermission(): String {
    return "${plugin.name.lowercase()}.commands.use"
}

fun commandNodePermission(commandRoot: String, vararg nodes: String): String {
    val path = buildList {
        add(commandRoot.lowercase())
        nodes.forEach { add(it.lowercase()) }
    }
    return "${plugin.name.lowercase()}.commands.${path.joinToString(".")}"
}

fun CommandDsl.withUsePermission() {
    permission(commandsUsePermission())
}

fun CommandDsl.withGeneratedPermission(commandRoot: String, vararg nodes: String) {
    permission(commandNodePermission(commandRoot, *nodes))
}
