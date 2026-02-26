package org.sayandev.stickynote.velocity.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.MutableCommandBuilder
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.setting.ManagerSetting
import org.incendo.cloud.suggestion.Suggestion
import org.incendo.cloud.velocity.VelocityCommandManager
import org.sayandev.stickynote.core.command.Command
import org.sayandev.stickynote.velocity.launch
import org.sayandev.stickynote.velocity.plugin
import java.util.concurrent.CompletableFuture

fun commandManager(): VelocityCommandManager<VelocitySender> {
    val velocitySenderMapper = { commandSender: CommandSource -> VelocitySender(commandSender) }
    val backwardsMapper = { sayanSenderExtension: VelocitySender -> sayanSenderExtension.platformSender() }

    val manager = VelocityCommandManager(
        plugin.container,
        plugin.server,
        ExecutionCoordinator.simpleCoordinator(),
        SenderMapper.create(velocitySenderMapper, backwardsMapper),
    )
    manager.settings().set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true)
    return manager
}

abstract class VelocityCommand(
    name: String,
    vararg aliases: String
) : Command<VelocitySender>(
    plugin.container.description.name.get(),
    commandManager(),
    name,
    *aliases
) {
    init {
        initializeManagerAndRoot()
    }
}

fun CommandContext<VelocitySender>.player(): Player? {
    return this.sender().player()
}

fun CommandContext<VelocitySender>.platformSender(): CommandSource {
    return this.sender().platformSender()
}

fun MutableCommandBuilder<VelocitySender>.literalWithPermission(literal: String) {
    literal(literal)
    permission("${plugin.container.description.name.get().lowercase()}.commands.${this.build().components().joinToString(".") { it.name() }}")
}

internal fun CommandComponent.Builder<VelocitySender, String>.createStringSuggestion(suggestions: Collection<String>) {
    this.suggestionProvider { _, _ ->
        CompletableFuture.completedFuture(suggestions.map { Suggestion.suggestion(it) })
    }
}

fun MutableCommandBuilder<VelocitySender>.required(name: String, suggestions: Collection<String>): MutableCommandBuilder<VelocitySender> {
    return required(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}

fun MutableCommandBuilder<VelocitySender>.optional(name: String, suggestions: Collection<String>): MutableCommandBuilder<VelocitySender> {
    return optional(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}

fun <S : Any> MutableCommandBuilder<S>.suspendingHandler(context: suspend (CommandContext<S>) -> Unit) {
    this.handler {
        launch {
            context(it)
        }
    }
}