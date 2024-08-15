package org.sayandev.stickynote.bukkit.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.Command
import org.incendo.cloud.CommandManager
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.execution.CommandExecutionHandler
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.MutableCommandBuilder
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import org.incendo.cloud.minecraft.extras.MinecraftHelp
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.setting.ManagerSetting
import org.incendo.cloud.suggestion.Suggestion
import org.sayandev.stickynote.bukkit.command.interfaces.CommandExtension
import org.sayandev.stickynote.bukkit.command.interfaces.SenderExtension
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.utils.AdventureUtils
import org.sayandev.stickynote.bukkit.utils.AdventureUtils.component
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.bukkit.warn
import java.util.concurrent.CompletableFuture

abstract class StickyCommand(
    val name: String,
    vararg val aliases: String
) : CommandExtension {

    private var errorPrefix = "<dark_gray>[</dark_gray><dark_red><bold>✘</bold></dark_red><dark_gray>]</dark_gray><gradient:dark_red:red>".component()

    val manager: CommandManager<StickySender>
    var builder: Command.Builder<StickySender>
    var help: MinecraftHelp<StickySender>
    var exceptionHandler: MinecraftExceptionHandler<SenderExtension>

    init {
        val stickySenderMapper = { commandSender: CommandSender -> StickySender(commandSender, null) }
        val backwardsMapper = { sayanSenderExtension: SenderExtension -> sayanSenderExtension.bukkitSender() }
        val audienceMapper = { sayanSenderExtension: SenderExtension -> AdventureUtils.audience.sender(sayanSenderExtension.bukkitSender()) }

        manager = if ((ServerVersion.supports(20) && ServerVersion.patchNumber() >= 5) || ServerVersion.supports(21)) {
            val modernMapper = { sourceStack: CommandSourceStack -> StickySender(sourceStack.sender, sourceStack) }
            val sourceMapper = { stickySender: StickySender -> stickySender.sourceStack!! }
            PaperCommandManager.builder(SenderMapper.create(modernMapper, sourceMapper))
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(plugin)
        } else {
            LegacyPaperCommandManager(
                plugin,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.create(stickySenderMapper, backwardsMapper),
            )
        }

        exceptionHandler = MinecraftExceptionHandler.create(audienceMapper)

        manager.createHelpHandler()
        manager.settings().set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true)
        try {
            if (manager is LegacyPaperCommandManager) {
                manager.registerAsynchronousCompletions()
            }
        } catch (_: IllegalStateException) { }
        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            if (manager is LegacyPaperCommandManager) {
                manager.registerBrigadier()
            }
        }

        help = MinecraftHelp.create(
            name,
            manager,
            audienceMapper
        )

        builder = manager.commandBuilder(name, *aliases)
    }

    override fun errorPrefix(): Component {
        return errorPrefix
    }

    override fun errorPrefix(prefix: Component) {
        errorPrefix = prefix
    }
}

internal fun CommandComponent.Builder<*, *>.createStringSuggestion(suggestions: Collection<String>) {
    this.suggestionProvider { context, input ->
        CompletableFuture.completedFuture(suggestions.map { Suggestion.suggestion(it) })
    }
}

fun MutableCommandBuilder<SenderExtension>.required(name: String, suggestions: Collection<String>): MutableCommandBuilder<SenderExtension> {
    return required(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}

fun MutableCommandBuilder<SenderExtension>.optional(name: String, suggestions: Collection<String>): MutableCommandBuilder<SenderExtension> {
    return optional(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}

fun CommandContext<SenderExtension>.player(): Player? {
    return this.sender().player()
}

fun CommandContext<SenderExtension>.sender(): CommandSender {
    return this.sender().bukkitSender()
}

fun CommandContext<SenderExtension>.audience(): Audience {
    return this.sender().audience()
}

fun MutableCommandBuilder<SenderExtension>.literalWithPermission(literal: String) {
    val parentComponents = this.build().components()
//    parentComponents.removeFirst()
    for (command in parentComponents) {
        warn("name: ${command.name()}")
    }
}