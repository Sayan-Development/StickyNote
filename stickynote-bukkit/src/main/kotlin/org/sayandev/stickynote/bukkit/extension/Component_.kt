package org.sayandev.stickynote.bukkit.extension

import org.sayandev.sayanventure.adventure.platform.bukkit.MinecraftComponentSerializer
import org.sayandev.sayanventure.adventure.text.Component

fun Component.toNmsComponent(): Any {
    return MinecraftComponentSerializer.get().serialize(this)
}