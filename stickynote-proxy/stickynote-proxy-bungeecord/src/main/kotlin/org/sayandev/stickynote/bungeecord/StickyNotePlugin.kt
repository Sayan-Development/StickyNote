package org.sayandev.stickynote.bungeecord

import net.md_5.bungee.api.plugin.Plugin

val plugin = wrappedPlugin.plugin

abstract class StickyNotePlugin constructor(plugin: Plugin, exclusiveThreads: Int) : WrappedStickyNotePlugin(plugin, exclusiveThreads) {
    constructor(plugin: Plugin) : this(plugin, 1)
}