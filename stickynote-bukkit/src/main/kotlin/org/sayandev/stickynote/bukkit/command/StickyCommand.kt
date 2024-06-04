package org.sayandev.stickynote.bukkit.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.incendo.cloud.Command
import org.incendo.cloud.CommandManager
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import org.incendo.cloud.minecraft.extras.MinecraftHelp
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.paper.PaperCommandManager
import org.sayandev.stickynote.bukkit.command.interfaces.CommandExtension
import org.sayandev.stickynote.bukkit.command.interfaces.SenderExtension
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.utils.AdventureUtils
import org.sayandev.stickynote.bukkit.utils.AdventureUtils.component
import org.sayandev.stickynote.bukkit.utils.ServerVersion

abstract class StickyCommand(
    val name: String,
    vararg val aliases: String
) : CommandExtension {

    private var errorPrefix = "<dark_gray>[</dark_gray><dark_red><bold>✘</bold></dark_red><dark_gray>]</dark_gray><gradient:dark_red:red>".component()

    val manager: CommandManager<StickySender>
    var builder: Command.Builder<StickySender>
    var help: MinecraftHelp<StickySender>
    var exceptionHandler: MinecraftExceptionHandler<SenderExtension>
//    var annotationParser: AnnotationParser<SenderExtension>

    init {
        val stickySenderMapper = { commandSender: CommandSender -> StickySender(commandSender, null) }
        val backwardsMapper = { sayanSenderExtension: SenderExtension -> sayanSenderExtension.bukkitSender() }
        val audienceMapper = { sayanSenderExtension: SenderExtension -> AdventureUtils.audience.sender(sayanSenderExtension.bukkitSender()) }

        manager = if (ServerVersion.supports(20) && ServerVersion.patchNumber() >= 5) {
            val modernMapper = { sourceStack: CommandSourceStack -> StickySender(sourceStack.sender, sourceStack) }
            val sourceMapper = { stickySender: StickySender -> stickySender.sourceStack!! }
            PaperCommandManager.builder(SenderMapper.create(modernMapper, sourceMapper))
                .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
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

//        annotationParser = AnnotationParser(manager, TypeToken.get(SenderExtension::class.java))
//        annotationParser.installCoroutineSupport()
//        annotationParser.parse(this)

        builder = manager.commandBuilder(name, *aliases)
    }

    override fun errorPrefix(): Component {
        return errorPrefix
    }

    override fun errorPrefix(prefix: Component) {
        errorPrefix = prefix
    }
}