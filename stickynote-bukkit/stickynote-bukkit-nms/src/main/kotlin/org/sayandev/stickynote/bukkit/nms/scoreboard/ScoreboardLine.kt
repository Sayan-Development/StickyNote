package org.sayandev.stickynote.bukkit.nms.scoreboard

import org.sayandev.sayanventure.adventure.text.Component

open class ScoreboardLine(
    val component: Component,
    internal val scorePacket: Any,
    internal val teamPacket: Any,
    internal val removeTeamPacket: Any
)