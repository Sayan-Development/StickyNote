package org.sayandev.stickynote.bukkit

import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.java.JavaPlugin

val plugin = wrappedPlugin.main

abstract class StickyNotePlugin : JavaPlugin() {
    init {
        WrappedStickyNotePlugin(this)
    }
}