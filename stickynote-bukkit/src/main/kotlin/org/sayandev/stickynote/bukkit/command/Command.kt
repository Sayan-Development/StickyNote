package org.sayandev.stickynote.bukkit.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
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
import org.sayandev.stickynote.bukkit.command.interfaces.CommandExtension
import org.sayandev.stickynote.bukkit.command.interfaces.SenderExtension
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.utils.AdventureUtils
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

abstract class Command(
    val name: String,
    vararg val aliases: String
) : CommandExtension {

    open fun rootBuilder(builder: MutableCommandBuilder<StickySender>) { }
    open fun rootHandler(context: CommandContext<StickySender>) { }

    private var errorPrefix = Component.empty().asComponent()

    val manager: CommandManager<StickySender>
    val help: MinecraftHelp<StickySender>
    val exceptionHandler: MinecraftExceptionHandler<SenderExtension>
    val command: MutableCommandBuilder<StickySender>

    fun registerHelpLiteral() {
        command.registerCopy {
            literal("help")
            optional("query", StringArrayParser.stringArrayParser())
            handler { context ->
                help.queryCommands(context.optional<Array<String>>("query").getOrNull()?.joinToString(" ")?.let { args ->
                    "$name $args"
                } ?: "", context.sender())
            }
        }
    }

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

        command = manager.buildAndRegister(name, Description.empty(), aliases.toList().toTypedArray()) {
            permission("${plugin.name.lowercase()}.commands.${name}")
            handler { context ->
                rootHandler(context)
            }
            rootBuilder(this)
        }
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

fun MutableCommandBuilder<StickySender>.required(name: String, suggestions: Collection<String>): MutableCommandBuilder<StickySender> {
    return required(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}

fun MutableCommandBuilder<StickySender>.optional(name: String, suggestions: Collection<String>): MutableCommandBuilder<StickySender> {
    return optional(name, StringParser.stringParser()) {
        createStringSuggestion(suggestions)
    }
}

fun CommandContext<StickySender>.player(): Player? {
    return this.sender().player()
}

fun CommandContext<StickySender>.bukkitSender(): CommandSender {
    return this.sender().bukkitSender()
}

fun CommandContext<StickySender>.audience(): Audience {
    return this.sender().audience()
}

fun MutableCommandBuilder<StickySender>.literalWithPermission(literal: String) {
    literal(literal)
    permission("${plugin.name.lowercase()}.commands.${this.build().components().joinToString(".") { it.name() }}")
}