package org.sayandev.stickynote.bungeecord

import com.google.common.util.concurrent.ThreadFactoryBuilder
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.scheduler.TaskScheduler
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

object StickyNote {

    private val threadFactory = ThreadFactoryBuilder().setNameFormat("sticky-note-async-thread-%d").build()
    private val asyncExecutor = Executors.newFixedThreadPool(wrappedPlugin.exclusiveThreads, threadFactory)

    @JvmStatic
    var debug = false

    @JvmStatic
    val logger = plugin().logger

    @JvmStatic
    val server = plugin().proxy

    @JvmStatic
    val dataDirectory = plugin().dataFolder

    @JvmStatic
    fun log(message: String) {
        logger.info(message)
    }

    @JvmStatic
    fun log(message: () -> String) {
        log(message())
    }

    @JvmStatic
    fun warn(message: String) {
        logger.warning(message)
    }

    @JvmStatic
    fun warn(message: () -> String) {
        warn(message())
    }

    @JvmStatic
    fun error(message: String) {
        logger.severe(message)
    }

    @JvmStatic
    fun error(message: () -> String) {
        error(message())
    }

    @JvmStatic
    fun debug(message: String) {
        if (debug) {
            logger.info("[DEBUG] $message")
        }
    }

    @JvmStatic
    fun debug(message: () -> String) {
        if (debug) {
            debug(message())
        }
    }

    @JvmStatic
    fun scheduler(): TaskScheduler {
        return plugin().proxy.scheduler
    }

    @JvmStatic
    fun run(runnable: Runnable) {
        scheduler().schedule(plugin(), runnable, 0, TimeUnit.MILLISECONDS)
    }

    @JvmStatic
    fun run(runnable: Runnable, delay: Long, unit: TimeUnit) {
        scheduler().schedule(plugin(), runnable, delay, unit)
    }

    @JvmStatic
    fun run(runnable: Runnable, delay: Long, period: Long, unit: TimeUnit) {
        scheduler().schedule(plugin(), runnable, delay, period, unit)
    }

    @JvmStatic
    fun runAsync(runnable: Runnable) {
        scheduler().runAsync(plugin(), runnable)
    }

    @JvmStatic
    fun runEAsync(runnable: Runnable): Future<*> {
        return asyncExecutor.submit(runnable)
    }

    @JvmStatic
    fun registerListener(listener: Listener) {
        server.pluginManager.registerListener(plugin(), listener)
    }

    @JvmStatic
    fun unregisterListener(listener: Listener) {
        server.pluginManager.unregisterListener(listener)
    }

    @JvmStatic
    fun hasPlugin(name: String): Boolean {
        return server.pluginManager.getPlugin(name) != null
    }

    @JvmStatic
    fun hasPlugins(vararg names: String): Boolean {
        return names.all { hasPlugin(it) }
    }

    @JvmStatic
    fun onlinePlayers(): MutableCollection<out ProxiedPlayer> = server.players

    @JvmStatic
    fun getPlayer(name: String): ProxiedPlayer? {
        return server.getPlayer(name)
    }

    @JvmStatic
    fun getPlayer(uniqueId: UUID): ProxiedPlayer? {
        return server.getPlayer(uniqueId)
    }

    @JvmStatic
    fun shutdown() {
        asyncExecutor.shutdown()
    }

    @JvmStatic
    fun plugin() = wrappedPlugin.plugin
}

fun run(runnable: Runnable) {
    StickyNote.run(runnable)
}

fun run(runnable: Runnable, delay: Long, unit: TimeUnit) {
    StickyNote.run(runnable, delay, unit)
}

fun run(runnable: Runnable, delay: Long, period: Long, unit: TimeUnit) {
    StickyNote.run(runnable, delay, period, unit)
}

fun runAsync(runnable: Runnable) {
    StickyNote.runAsync(runnable)
}

fun runEAsync(runnable: Runnable): Future<*> {
    return StickyNote.runEAsync(runnable)
}

fun registerListener(listener: Listener) {
    StickyNote.registerListener(listener)
}

fun unregisterListener(listener: Listener) {
    StickyNote.unregisterListener(listener)
}

fun log(message: String) {
    StickyNote.log(message)
}

fun log(message: () -> String) {
    StickyNote.log(message)
}

fun warn(message: String) {
    StickyNote.warn(message)
}

fun warn(message: () -> String) {
    StickyNote.warn(message)
}

fun error(message: String) {
    StickyNote.error(message)
}

fun error(message: () -> String) {
    StickyNote.error(message)
}

fun debug(message: String) {
    StickyNote.debug(message)
}

fun debug(message: () -> String) {
    StickyNote.debug(message)
}

fun hasPlugin(name: String): Boolean {
    return StickyNote.hasPlugin(name)
}

fun hasPlugins(vararg name: String): Boolean {
    return StickyNote.hasPlugins(*name)
}

val server = StickyNote.server
val dataDirectory = StickyNote.dataDirectory
val onlinePlayers = StickyNote.onlinePlayers()