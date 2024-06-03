package org.sayandev.stickynote.bukkit.command

import io.leangen.geantyref.TypeToken
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.incendo.cloud.Command
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.coroutines.annotations.installCoroutineSupport
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import org.incendo.cloud.minecraft.extras.MinecraftHelp
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.sayandev.stickynote.bukkit.command.interfaces.CommandExtension
import org.sayandev.stickynote.bukkit.command.interfaces.SenderExtension
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.utils.AdventureUtils
import org.sayandev.stickynote.bukkit.utils.AdventureUtils.component

abstract class StickyCommand(
    val name: String,
    vararg val aliases: String
) : CommandExtension {

    private var errorPrefix = "<dark_gray>[</dark_gray><dark_red><bold>✘</bold></dark_red><dark_gray>]</dark_gray><gradient:dark_red:red>".component()

    var manager: LegacyPaperCommandManager<SenderExtension>
    var builder: Command.Builder<SenderExtension>
    var help: MinecraftHelp<SenderExtension>
    var exceptionHandler: MinecraftExceptionHandler<SenderExtension>
    var annotationParser: AnnotationParser<SenderExtension>

    init {
        val stickySenderMapper = { commandSender: CommandSender -> StickySender(commandSender) }
        val backwardsMapper = { sayanSenderExtension: SenderExtension -> sayanSenderExtension.bukkitSender() }
        val audienceMapper = { sayanSenderExtension: SenderExtension -> AdventureUtils.audience.sender(sayanSenderExtension.bukkitSender()) }

        manager = LegacyPaperCommandManager(
            plugin,
            ExecutionCoordinator.simpleCoordinator(),
            SenderMapper.create(stickySenderMapper, backwardsMapper),
        )

        exceptionHandler = MinecraftExceptionHandler.create(audienceMapper)

        manager.createHelpHandler()
        try {
            manager.registerAsynchronousCompletions()
        } catch (_: IllegalStateException) { }
        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            manager.registerBrigadier()
        }

        help = MinecraftHelp.create(
            name,
            manager,
            audienceMapper
        )

        annotationParser = AnnotationParser(manager, TypeToken.get(SenderExtension::class.java))
        annotationParser.installCoroutineSupport()
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