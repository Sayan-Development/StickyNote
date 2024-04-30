package org.sayandevelopment.stickynote.bukkit

import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: StickyNotePlugin

abstract class StickyNotePlugin(val exclusiveThreads: Int) : JavaPlugin() {

    constructor() : this(1)

    init {
        plugin = this
    }

    override fun onDisable() {
        StickyNote.shutdown()
    }

    companion object {
        @JvmStatic
        fun getPlugin(): StickyNotePlugin {
            return plugin
        }
    }
}