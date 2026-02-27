package org.sayandev.stickynote.paper.nms.scoreboard

import net.kyori.adventure.text.Component

open class ScoreboardLine(
    val component: Component,
    internal val scorePacket: Any,
    internal val teamPacket: Any,
    internal val removeTeamPacket: Any
)