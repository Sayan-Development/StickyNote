package org.sayandev.stickynote.bukkit

import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.slf4j.LoggerFactory
import java.lang.Runnable
import java.util.concurrent.Executors
import java.util.concurrent.Future

object StickyNote {

    private val threadFactory = ThreadFactoryBuilder().setNameFormat("sticky-note-${org.sayandev.stickynote.bukkit.plugin.name}-async-thread-%d").build()
    private val asyncExecutor = Executors.newFixedThreadPool(
        org.sayandev.stickynote.bukkit.plugin.exclusiveThreads,
        org.sayandev.stickynote.bukkit.StickyNote.threadFactory
    )

    @JvmStatic
    var debug = false

    @JvmStatic
    val logger = LoggerFactory.getLogger("stickynote")

    @JvmStatic
    fun log(message: String) {
        org.sayandev.stickynote.bukkit.StickyNote.logger.info(message)
    }

    @JvmStatic
    fun log(message: () -> String) {
        org.sayandev.stickynote.bukkit.StickyNote.log(message())
    }

    @JvmStatic
    fun warn(message: String) {
        org.sayandev.stickynote.bukkit.StickyNote.logger.warn(message)
    }

    @JvmStatic
    fun warn(message: () -> String) {
        org.sayandev.stickynote.bukkit.StickyNote.warn(message())
    }

    @JvmStatic
    fun error(message: String) {
        org.sayandev.stickynote.bukkit.StickyNote.logger.error(message)
    }

    @JvmStatic
    fun error(message: () -> String) {
        org.sayandev.stickynote.bukkit.StickyNote.error(message())
    }

    @JvmStatic
    fun debug(message: String) {
        if (org.sayandev.stickynote.bukkit.StickyNote.debug) {
            org.sayandev.stickynote.bukkit.StickyNote.logger.debug(message)
        }
    }

    @JvmStatic
    fun debug(message: () -> String) {
        if (org.sayandev.stickynote.bukkit.StickyNote.debug) {
            org.sayandev.stickynote.bukkit.StickyNote.debug(message())
        }
    }

    @JvmStatic
    fun runSync(runnable: Runnable) {
        org.sayandev.stickynote.bukkit.plugin.server.scheduler.runTask(org.sayandev.stickynote.bukkit.plugin, runnable)
    }

    @JvmStatic
    fun runSync(runnable: Runnable, delay: Long) {
        org.sayandev.stickynote.bukkit.plugin.server.scheduler.runTaskLater(org.sayandev.stickynote.bukkit.plugin, runnable, delay)
    }

    @JvmStatic
    fun runSync(runnable: Runnable, delay: Long, period: Long) {
        org.sayandev.stickynote.bukkit.plugin.server.scheduler.runTaskTimer(org.sayandev.stickynote.bukkit.plugin, runnable, delay, period)
    }

    @JvmStatic
    fun runAsync(runnable: Runnable) {
        org.sayandev.stickynote.bukkit.plugin.server.scheduler.runTaskAsynchronously(org.sayandev.stickynote.bukkit.plugin, runnable)
    }

    @JvmStatic
    fun runAsync(runnable: Runnable, delay: Long) {
        org.sayandev.stickynote.bukkit.plugin.server.scheduler.runTaskLaterAsynchronously(org.sayandev.stickynote.bukkit.plugin, runnable, delay)
    }

    @JvmStatic
    fun runAsync(runnable: Runnable, delay: Long, period: Long) {
        org.sayandev.stickynote.bukkit.plugin.server.scheduler.runTaskTimerAsynchronously(org.sayandev.stickynote.bukkit.plugin, runnable, delay, period)
    }

    @JvmStatic
    fun runEAsync(runnable: Runnable): Future<*> {
        return org.sayandev.stickynote.bukkit.StickyNote.asyncExecutor.submit(runnable)
    }

    @JvmStatic
    fun registerListener(listener: Listener) {
        org.sayandev.stickynote.bukkit.plugin.server.pluginManager.registerEvents(listener,
            org.sayandev.stickynote.bukkit.plugin
        )
    }

    @JvmStatic
    fun unregisterListener(listener: Listener) {
        HandlerList.unregisterAll(listener)
    }

    @JvmStatic
    fun unregisterAllListeners() {
        HandlerList.unregisterAll(org.sayandev.stickynote.bukkit.plugin)
    }

    @JvmStatic
    fun hasPlugin(name: String): Boolean {
        return org.sayandev.stickynote.bukkit.plugin.server.pluginManager.getPlugin(name) != null
    }

    @JvmStatic
    fun hasPlugins(vararg name: String): Boolean {
        return name.all { org.sayandev.stickynote.bukkit.StickyNote.hasPlugin(it) }
    }

    @JvmStatic
    fun plugin() = org.sayandev.stickynote.bukkit.plugin

    @JvmStatic
    fun server() = org.sayandev.stickynote.bukkit.plugin.server

    @JvmStatic
    fun pluginDirectory() = org.sayandev.stickynote.bukkit.plugin.dataFolder

    @JvmStatic
    fun onlinePlayers(): MutableCollection<out Player> = org.sayandev.stickynote.bukkit.plugin.server.onlinePlayers

    @JvmStatic
    fun shutdown() {
        org.sayandev.stickynote.bukkit.StickyNote.unregisterAllListeners()
        org.sayandev.stickynote.bukkit.StickyNote.asyncExecutor.shutdown()
    }

}

fun runSync(runnable: Runnable) {
    org.sayandev.stickynote.bukkit.StickyNote.runSync(runnable)
}

fun runSync(runnable: Runnable, delay: Long) {
    org.sayandev.stickynote.bukkit.StickyNote.runSync(runnable, delay)
}

fun runSync(runnable: Runnable, delay: Long, period: Long) {
    org.sayandev.stickynote.bukkit.StickyNote.runSync(runnable, delay, period)
}

fun runAsync(runnable: Runnable) {
    org.sayandev.stickynote.bukkit.StickyNote.runAsync(runnable)
}

fun runAsync(runnable: Runnable, delay: Long) {
    org.sayandev.stickynote.bukkit.StickyNote.runAsync(runnable, delay)
}

fun runAsync(runnable: Runnable, delay: Long, period: Long) {
    org.sayandev.stickynote.bukkit.StickyNote.runAsync(runnable, delay, period)
}

fun runEAsync(runnable: Runnable): Future<*> {
    return org.sayandev.stickynote.bukkit.StickyNote.runEAsync(runnable)
}

fun registerListener(listener: Listener) {
    org.sayandev.stickynote.bukkit.StickyNote.registerListener(listener)
}

fun unregisterListener(listener: Listener) {
    org.sayandev.stickynote.bukkit.StickyNote.unregisterListener(listener)
}

fun log(message: String) {
    org.sayandev.stickynote.bukkit.StickyNote.log(message)
}

fun log(message: () -> String) {
    org.sayandev.stickynote.bukkit.StickyNote.log(message)
}

fun warn(message: String) {
    org.sayandev.stickynote.bukkit.StickyNote.warn(message)
}

fun warn(message: () -> String) {
    org.sayandev.stickynote.bukkit.StickyNote.warn(message)
}

fun error(message: String) {
    org.sayandev.stickynote.bukkit.StickyNote.error(message)
}

fun error(message: () -> String) {
    org.sayandev.stickynote.bukkit.StickyNote.error(message)
}

fun debug(message: String) {
    org.sayandev.stickynote.bukkit.StickyNote.debug(message)
}

fun debug(message: () -> String) {
    org.sayandev.stickynote.bukkit.StickyNote.debug(message)
}

fun hasPlugin(name: String): Boolean {
    return org.sayandev.stickynote.bukkit.StickyNote.hasPlugin(name)
}

fun hasPlugins(vararg name: String): Boolean {
    return org.sayandev.stickynote.bukkit.StickyNote.hasPlugins(*name)
}

val server = org.sayandev.stickynote.bukkit.StickyNote.server()
val pluginDirectory = org.sayandev.stickynote.bukkit.StickyNote.pluginDirectory()
val onlinePlayers = org.sayandev.stickynote.bukkit.StickyNote.onlinePlayers()