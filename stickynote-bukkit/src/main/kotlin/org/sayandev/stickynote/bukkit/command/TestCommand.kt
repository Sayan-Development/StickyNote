package org.sayandev.stickynote.bukkit.command

import org.incendo.cloud.description.Description
import org.incendo.cloud.kotlin.extension.buildAndRegister
import org.sayandev.stickynote.bukkit.command.audience

class TestCommand : StickyCommand("sosis") {

    init {
        val command = manager.buildAndRegister("sosis", Description.empty(), arrayOf()) {
            handler { context ->
            }
        }
    }
}