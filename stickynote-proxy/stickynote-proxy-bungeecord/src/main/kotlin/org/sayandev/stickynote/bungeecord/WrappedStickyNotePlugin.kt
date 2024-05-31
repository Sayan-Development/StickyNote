package org.sayandev.stickynote.bungeecord

import net.md_5.bungee.api.plugin.Plugin

lateinit var wrappedPlugin: WrappedStickyNotePlugin

open class WrappedStickyNotePlugin constructor(
    val plugin: Plugin,
    val exclusiveThreads: Int,
)  {
    constructor(plugin: Plugin) : this(plugin, 1)

    init {
        wrappedPlugin = this
    }

    fun initialize() {
        onInitialize()
    }

    open fun onInitialize() {
    }

    companion object {
        @JvmStatic
        fun getPlugin(): WrappedStickyNotePlugin {
            return wrappedPlugin
        }
    }
}