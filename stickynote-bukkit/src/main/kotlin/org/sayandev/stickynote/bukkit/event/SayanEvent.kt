package org.sayandev.stickynote.bukkit.event

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.sayandev.stickynote.bukkit.plugin

inline fun <reified T: Event> registerListener(
    priority: EventPriority = EventPriority.HIGH,
    ignoreCancelled: Boolean = false,
    crossinline run: (T) -> Unit
) {
    val event = object : ObjectiveEvent<T>(T::class.java) {
        override fun execute(event: T) {
            Unit.run { run(event) }
        }
    }
    Bukkit.getPluginManager().registerEvent(
        T::class.java,
        event,
        priority,
        event,
        plugin,
        ignoreCancelled
    )
}