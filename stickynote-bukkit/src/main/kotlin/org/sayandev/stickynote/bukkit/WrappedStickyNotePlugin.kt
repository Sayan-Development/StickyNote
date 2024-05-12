package org.sayandev.stickynote.bukkit

import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.java.JavaPlugin

lateinit var wrappedPlugin: WrappedStickyNotePlugin

class WrappedStickyNotePlugin(val main: JavaPlugin, val exclusiveThreads: Int) {

    constructor(main: JavaPlugin) : this(main, 1)

    init {
        wrappedPlugin = this
    }

    companion object {
        @JvmStatic
        fun getPlugin(): WrappedStickyNotePlugin {
            return wrappedPlugin
        }
    }
}