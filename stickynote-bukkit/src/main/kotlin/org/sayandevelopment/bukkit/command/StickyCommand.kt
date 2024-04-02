package org.sayandevelopment.bukkit.command

import ir.syrent.origin.paper.command.OriginSenderExtension
import org.sayandevelopment.bukkit.command.interfaces.CommandExtension
import org.sayandevelopment.bukkit.command.interfaces.SenderExtension
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.incendo.cloud.Command
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import org.incendo.cloud.minecraft.extras.MinecraftHelp
import org.incendo.cloud.paper.PaperCommandManager
import org.sayandevelopment.bukkit.plugin
import org.sayandevelopment.bukkit.utils.AdventureUtils
import org.sayandevelopment.bukkit.utils.AdventureUtils.component

abstract class StickyCommand(
    val name: String,
    vararg val aliases: String
) : CommandExtension {

    private var errorPrefix = "<dark_gray>[</dark_gray><dark_red><bold>✘</bold></dark_red><dark_gray>]</dark_gray><gradient:dark_red:red>".component()

    var manager: PaperCommandManager<SenderExtension>
    var builder: Command.Builder<SenderExtension>
    var help: MinecraftHelp<SenderExtension>
    var exceptionHandler: MinecraftExceptionHandler<SenderExtension>

    init {
        val originSenderMapper = { commandSender: CommandSender -> OriginSenderExtension(commandSender) }
        val backwardsMapper = { sayanSenderExtension: SenderExtension -> sayanSenderExtension.sender() }
        val audienceMapper = { sayanSenderExtension: SenderExtension -> AdventureUtils.audience.sender(sayanSenderExtension.sender()) }

        manager = PaperCommandManager(
            plugin,
            ExecutionCoordinator.simpleCoordinator(),
            SenderMapper.create(originSenderMapper, backwardsMapper),
        )

        exceptionHandler = MinecraftExceptionHandler.create(audienceMapper)

        manager.createHelpHandler()
        manager.registerAsynchronousCompletions()
        manager.registerBrigadier()

        help = MinecraftHelp.create(
            "/${name} help",
            manager,
            audienceMapper
        )

        builder = manager.commandBuilder(name, *aliases).permission(constructBasePermission(name))
    }

    override fun errorPrefix(): Component {
        return errorPrefix
    }

    override fun errorPrefix(prefix: Component) {
        errorPrefix = prefix
    }
}