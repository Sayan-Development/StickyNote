package org.sayandev.stickynote.paper.extension

import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component

fun Component.toNmsComponent(): Any {
    return MinecraftComponentSerializer.get().serialize(this)
}