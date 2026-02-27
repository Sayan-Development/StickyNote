package org.sayandev.stickynote.paper.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.CommandManager
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.brigadier.BrigadierSetting
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.description.Description
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.MutableCommandBuilder
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.setting.ManagerSetting
import org.incendo.cloud.suggestion.Suggestion
import org.sayandev.stickynote.paper.launch
import org.sayandev.stickynote.paper.plugin
import org.sayandev.stickynote.paper.utils.ServerVersion
import org.sayandev.stickynote.core.command.Command
import java.util.concurrent.CompletableFuture

fun commandManager(): CommandManager<PaperSender> {
    val paperSenderMapper = { commandSender: CommandSender -> PaperSender(commandSender, null) }
    val backwardsMapper = { senderExtension: PaperSender -> senderExtension.platformSender() }
    val manager = if (ServerVersion.isAtLeast(20, 5)) {
        val modernMapper = { sourceStack: CommandSourceStack -> PaperSender(sourceStack.sender, sourceStack) }
        val sourceMapper = { paperSender: PaperSender -> paperSender.sourceStack!! }
        PaperCommandManager.builder(SenderMapper.create(modernMapper, sourceMapper))
            .executionCoordinator(ExecutionCoordinator.coordinatorFor(ExecutionCoordinator.nonSchedulingExecutor()))
            .buildOnEnable(plugin)
            .apply {
                this.brigadierManager().settings().set(BrigadierSetting.FORCE_EXECUTABLE,  true)
            }
    } else {
        LegacyPaperCommandManager(
            plugin,
            ExecutionCoordinator.coordinatorFor(ExecutionCoordinator.nonSchedulingExecutor()),
            SenderMapper.create(paperSenderMapper, backwardsMapper),
        ).apply {
            if (this.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
                this.registerBrigadier()
            }
            if (this.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                this.registerAsynchronousCompletions()
            }
        }
    }
    manager.settings().set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true)
    return manager
}

abstract class PaperCommand(
    name: String,
    vararg aliases: String
) : Command<PaperSender>(
    plugin.name,
    commandManager(),
    name,
    *aliases
) {

    init {
        initializeManagerAndRoot()
    }

    fun <S : Any> MutableCommandBuilder<S>.suspendingHandler(context: suspend (CommandContext<S>) -> Unit) {
        this.handler {
            launch {
                context(it)
            }
        }
    }
}

fun CommandContext<PaperSender>.player(): Player? {
    return this.sender().player()
}

fun CommandContext<PaperSender>.platformSender(): CommandSender {
    return this.sender().platformSender()
}

fun MutableCommandBuilder<PaperSender>.literalWithPermission(literal: String, vararg aliases: String) {
    literal(literal, Description.empty(), *aliases)
    permission("${plugin.name.lowercase()}.commands.${this.build().rootComponent().name().lowercase()}.${this.build().components().joinToString(".") { it.name() }}")
}

internal fun CommandComponent.Builder<PaperSender, String>.createStringSuggestion(suggestions: Collection<String>) {
    this.suggestionProvider { _, _ ->
        CompletableFuture.completedFuture(suggestions.map { Suggestion.suggestion(it) })
    }
}

fun MutableCommandBuilder<PaperSender>.required(name: String, suggestions: Collection<String>): MutableCommandBuilder<PaperSender> {
    return required(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}

fun MutableCommandBuilder<PaperSender>.optional(name: String, suggestions: Collection<String>): MutableCommandBuilder<PaperSender> {
    return optional(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}
