package org.sayandev.stickynote.velocity

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.velocitypowered.api.event.EventHandler
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.scheduler.Scheduler
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
    val server = plugin().server

    @JvmStatic
    val dataDirectory = plugin().dataDirectory

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
    fun scheduler(): Scheduler {
        return plugin().server.scheduler
    }

    @JvmStatic
    fun run(runnable: Runnable) {
        scheduler().buildTask(plugin, runnable).schedule()
    }

    @JvmStatic
    fun run(runnable: Runnable, delay: Long, unit: TimeUnit) {
        scheduler().buildTask(plugin, runnable).delay(delay, unit).schedule()
    }

    @JvmStatic
    fun run(runnable: Runnable, delay: Long, delayUnit: TimeUnit, period: Long, periodUnit: TimeUnit) {
        scheduler().buildTask(plugin, runnable).delay(delay, delayUnit).repeat(period, periodUnit).schedule()
    }

    @JvmStatic
    fun runEAsync(runnable: Runnable): Future<*> {
        return asyncExecutor.submit(runnable)
    }

    @JvmStatic
    fun registerListener(listener: Any) {
        server.eventManager.register(mainInstance, listener)
    }

    @JvmStatic
    fun unregisterListener(listener: EventHandler<*>) {
        server.eventManager.unregister(mainInstance, listener)
    }

    @JvmStatic
    fun hasPlugin(name: String): Boolean {
        return server.pluginManager.getPlugin(name).isPresent
    }

    @JvmStatic
    fun hasPlugins(vararg names: String): Boolean {
        return names.all { hasPlugin(it) }
    }

    @JvmStatic
    fun onlinePlayers(): MutableCollection<out Player> = server.allPlayers

    @JvmStatic
    fun getPlayer(name: String): Player? {
        return server.getPlayer(name).orElse(null)
    }

    @JvmStatic
    fun getPlayer(uniqueId: UUID): Player? {
        return server.getPlayer(uniqueId).orElse(null)
    }

    @JvmStatic
    fun shutdown() {
        asyncExecutor.shutdown()
    }

    @JvmStatic
    fun plugin() = plugin
}

fun run(runnable: Runnable) {
    StickyNote.run(runnable)
}

fun run(runnable: Runnable, delay: Long, unit: TimeUnit) {
    StickyNote.run(runnable, delay, unit)
}

fun run(runnable: Runnable, delay: Long, delayUnit: TimeUnit, period: Long, periodUnit: TimeUnit) {
    StickyNote.run(runnable, delay, delayUnit, period, periodUnit)
}

fun runEAsync(runnable: Runnable): Future<*> {
    return StickyNote.runEAsync(runnable)
}

fun registerListener(listener: Any) {
    StickyNote.registerListener(listener)
}

fun unregisterListener(listener: EventHandler<*>) {
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