package org.sayandev.stickynote.bukkit.nms.scoreboard

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component
import org.sayandev.stickynote.bukkit.nms.NMSUtils.sendPacket
import org.sayandev.stickynote.bukkit.nms.PacketUtils
import org.sayandev.stickynote.bukkit.nms.Viewable
import org.sayandev.stickynote.bukkit.nms.enum.CollisionRule
import org.sayandev.stickynote.bukkit.nms.enum.NameTagVisibility
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.bukkit.nms.accessors.*

open class Scoreboard(
    val id: String,
    title: Component
): Viewable() {

    private val lines: MutableMap<Int, ScoreboardLine> = mutableMapOf()
    private val objective: Any = createObjective(title)
    var title: Component = title; private set

    fun setLine(index: Int, component: Component) {
        val line = createLine(index, component)

        getViewers().sendPacket(line.scorePacket, line.teamPacket)

        lines[index] = line
    }

    /*TODO: fun setTitle(title: Component) {

    }*/

    private fun createLine(index: Int, component: Component): ScoreboardLine {
        return ScoreboardLine(
            component,
            PacketUtils.getSetScorePacket(id, COLOR_CODES[index], 0),
            getTeamCreatePacket(index, component),
            getTeamRemovePacket(index)
        )
    }

    private fun createObjective(title: Component): Any {
        val scoreboard: Any = ScoreboardAccessor.CONSTRUCTOR_0!!.newInstance()
        if (ServerVersion.isAtLeast(20, 3)) {
            ScoreboardAccessor.METHOD_ADD_OBJECTIVE_1!!.invoke(
                scoreboard,
                id,
                ObjectiveCriteriaAccessor.FIELD_TRIGGER!!,
                MinecraftComponentSerializer.get().serialize(title),
                ObjectiveCriteria_RenderTypeAccessor.FIELD_INTEGER!!,
                false,
                BlankFormatAccessor.FIELD_INSTANCE!!
            )
        } else {
            ScoreboardAccessor.METHOD_ADD_OBJECTIVE!!.invoke(
                scoreboard,
                id,
                ObjectiveCriteriaAccessor.FIELD_TRIGGER!!,
                MinecraftComponentSerializer.get().serialize(title),
                ObjectiveCriteria_RenderTypeAccessor.FIELD_INTEGER!!
            )
        }
        return ScoreboardAccessor.METHOD_GET_OBJECTIVE!!.invoke(scoreboard, id)
    }

    private fun getTeamCreatePacket(score: Int, line: Component): Any {
        return PacketUtils.getTeamCreatePacket(
            "$id:$score",
            line,
            Component.empty(),
            NameTagVisibility.NEVER,
            CollisionRule.NEVER,
            ChatColor.GRAY,
            listOf(COLOR_CODES[score]),
            false
        )
    }

    private fun getAddLinesPackets(): List<Any> {
        if (lines.isEmpty()) return emptyList()
        return buildList {
            for (i in 0..lines.keys.max()) {
                val line = if (lines.containsKey(i)) {
                    lines[i]!!
                } else {
                    createLine(i, Component.empty())
                }
                add(line.scorePacket)
                add(line.teamPacket)
            }
        }
    }

    private fun getRemoveLinesPackets(): List<Any> {
        return buildList {
            for (line in lines) {
                add(line.value.removeTeamPacket)
            }
        }
    }

    private fun getTeamRemovePacket(score: Int): Any {
        return PacketUtils.getTeamRemovePacket("$id:$score")
    }

    fun discard() {
        removeViewers(getViewers())
    }

    override fun addViewer(viewer: Player) {
        viewer.sendPacket(listOf(
            PacketUtils.getSetObjectivePacket(objective, 0),
            PacketUtils.getSetDisplayObjectivePacket(objective),
            *getAddLinesPackets().toTypedArray()
        ))
    }

    override fun removeViewer(viewer: Player) {
        viewer.sendPacket(listOf(
            PacketUtils.getSetObjectivePacket(objective, 1),
            PacketUtils.getSetDisplayObjectivePacket(objective),
            *getRemoveLinesPackets().toTypedArray()
        ))
    }

    companion object {
        val COLOR_CODES: Array<String> = ChatColor.entries.map { it.toString() }.toTypedArray()
    }
}