package org.sayandev.stickynote.bukkit.command

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
import org.sayandev.stickynote.bukkit.launch
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.core.command.Command
import java.util.concurrent.CompletableFuture

fun commandManager(): CommandManager<BukkitSender> {
    val bukkitSenderMapper = { commandSender: CommandSender -> BukkitSender(commandSender, null) }
    val backwardsMapper = { sayanSenderExtension: BukkitSender -> sayanSenderExtension.platformSender() }
    val manager = if (ServerVersion.isAtLeast(20, 5)) {
        val modernMapper = { sourceStack: CommandSourceStack -> BukkitSender(sourceStack.sender, sourceStack) }
        val sourceMapper = { bukkitSender: BukkitSender -> bukkitSender.sourceStack!! }
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
            SenderMapper.create(bukkitSenderMapper, backwardsMapper),
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

abstract class BukkitCommand(
    name: String,
    vararg aliases: String
) : Command<BukkitSender>(
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

fun CommandContext<BukkitSender>.player(): Player? {
    return this.sender().player()
}

fun CommandContext<BukkitSender>.platformSender(): CommandSender {
    return this.sender().platformSender()
}

fun MutableCommandBuilder<BukkitSender>.literalWithPermission(literal: String, vararg aliases: String) {
    literal(literal, Description.empty(), *aliases)
    permission("${plugin.name.lowercase()}.commands.${this.build().rootComponent().name().lowercase()}.${this.build().components().joinToString(".") { it.name() }}")
}

internal fun CommandComponent.Builder<BukkitSender, String>.createStringSuggestion(suggestions: Collection<String>) {
    this.suggestionProvider { _, _ ->
        CompletableFuture.completedFuture(suggestions.map { Suggestion.suggestion(it) })
    }
}

fun MutableCommandBuilder<BukkitSender>.required(name: String, suggestions: Collection<String>): MutableCommandBuilder<BukkitSender> {
    return required(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}

fun MutableCommandBuilder<BukkitSender>.optional(name: String, suggestions: Collection<String>): MutableCommandBuilder<BukkitSender> {
    return optional(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}