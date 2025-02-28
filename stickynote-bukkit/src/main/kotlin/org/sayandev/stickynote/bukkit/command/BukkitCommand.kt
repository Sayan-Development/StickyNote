package org.sayandev.stickynote.bukkit.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.CommandManager
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.description.Description
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.MutableCommandBuilder
import org.incendo.cloud.kotlin.extension.buildAndRegister
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import org.incendo.cloud.minecraft.extras.MinecraftHelp
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.parser.standard.StringArrayParser
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.setting.ManagerSetting
import org.incendo.cloud.suggestion.Suggestion
import org.sayandev.stickynote.bukkit.launch
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.utils.AdventureUtils
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.core.command.Command
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

fun commandManager(): CommandManager<BukkitSender> {
    val bukkitSenderMapper = { commandSender: CommandSender -> BukkitSender(commandSender, null) }
    val backwardsMapper = { sayanSenderExtension: BukkitSender -> sayanSenderExtension.platformSender() }
    val manager = if ((ServerVersion.supports(20) && ServerVersion.patchNumber() >= 5) || ServerVersion.supports(21)) {
        val modernMapper = { sourceStack: CommandSourceStack -> BukkitSender(sourceStack.sender, sourceStack) }
        val sourceMapper = { bukkitSender: BukkitSender -> bukkitSender.sourceStack!! }
        PaperCommandManager.builder(SenderMapper.create(modernMapper, sourceMapper))
            .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
            .buildOnEnable(plugin)
    } else {
        LegacyPaperCommandManager(
            plugin,
            ExecutionCoordinator.simpleCoordinator(),
            SenderMapper.create(bukkitSenderMapper, backwardsMapper),
        )
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

    private var errorPrefix = Component.empty().asComponent()

    val help: MinecraftHelp<BukkitSender>
    val exceptionHandler: MinecraftExceptionHandler<BukkitSender>

    fun registerHelpLiteral() {
        manager.buildAndRegister(name, Description.empty(), aliases.toList().toTypedArray()) {
            literalWithPermission("help")
            optional("query", StringArrayParser.stringArrayParser())
            handler { context ->
                help.queryCommands(context.optional<Array<String>>("query").getOrNull()?.joinToString(" ")?.let { args ->
                    "$name $args"
                } ?: "", context.sender())
            }
        }
    }

    init {
        try {
            (manager as? LegacyPaperCommandManager)?.registerAsynchronousCompletions()
        } catch (_: IllegalStateException) { }
        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            (manager as? LegacyPaperCommandManager)?.registerBrigadier()
        }

        val audienceMapper = { sayanSenderExtension: BukkitSender -> AdventureUtils.audience.sender(sayanSenderExtension.platformSender()) }
        exceptionHandler = MinecraftExceptionHandler.create(audienceMapper)

        help = MinecraftHelp.create(
            name,
            manager,
            audienceMapper
        )

        initializeManagerAndRoot()
    }

    override fun errorPrefix(): Component {
        return errorPrefix
    }

    override fun errorPrefix(prefix: TextComponent) {
        errorPrefix = prefix
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

fun CommandContext<BukkitSender>.audience(): Audience {
    return this.sender().audience()
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