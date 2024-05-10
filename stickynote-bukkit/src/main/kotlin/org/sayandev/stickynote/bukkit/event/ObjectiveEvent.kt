package org.sayandev.stickynote.bukkit.event

import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor

abstract class ObjectiveEvent<E: Event>(private val clazz: Class<*>) : EventExecutor, Listener {
    override fun execute(listener: Listener, event: Event) {
        if (!clazz.isAssignableFrom(event::class.java)) return
        this.execute(event as E)
    }

    abstract fun execute(event: E)
}

