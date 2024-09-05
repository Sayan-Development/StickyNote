package org.sayandev.stickynote.bukkit

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.github.shynixn.mccoroutine.folia.launch
import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.*
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.sayandev.stickynote.bukkit.async
import org.sayandev.stickynote.bukkit.coroutine.dispatcher.BukkitDispatcher
import org.sayandev.stickynote.bukkit.coroutine.dispatcher.FoliaDispatcher
import java.lang.Runnable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

object StickyNote {

    private val threadFactory = ThreadFactoryBuilder().setNameFormat("sticky-note-${plugin.name}-async-thread-%d").build()
    private val asyncExecutor = Executors.newFixedThreadPool(
        wrappedPlugin.exclusiveThreads,
        threadFactory
    )

    @JvmStatic
    fun isFolia(): Boolean = try {
        Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
        true
    } catch (e: Exception) {
        false
    }

    @JvmStatic
    fun isPaper(): Boolean = try {
        Class.forName("com.destroystokyo.paper.ParticleBuilder")
        true
    } catch (e: Exception) {
        false
    }

    @JvmStatic
    var debug = false

    @JvmStatic
    val logger = plugin().logger

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
    fun runSync(runnable: Runnable) {
        if (isFolia()) {
            plugin.server.globalRegionScheduler.runDelayed(plugin, {
                runnable.run()
            }, 1)
        } else {
            plugin.server.scheduler.runTask(plugin, runnable)
        }
    }

    @JvmStatic
    fun runSync(runnable: Runnable, delay: Long) {
        if (isFolia()) {
            plugin.server.globalRegionScheduler.runDelayed(plugin, {
                runnable.run()
            }, delay)
        } else {
            plugin.server.scheduler.runTaskLater(plugin, runnable, delay)
        }
    }

    @JvmStatic
    fun runSync(runnable: Runnable, delay: Long, period: Long) {
        if (isFolia()) {
            plugin.server.globalRegionScheduler.runAtFixedRate(plugin, {
                runnable.run()
            }, delay.coerceAtLeast(1), period)
        } else {
            plugin.server.scheduler.runTaskTimer(plugin, runnable, delay, period)
        }
    }

    @JvmStatic
    fun runAsync(runnable: Runnable) {
        if (isFolia()) {
            plugin.server.asyncScheduler.runNow(plugin) {
                runnable.run()
            }
        } else {
            plugin.server.scheduler.runTaskAsynchronously(plugin, runnable)
        }
    }

    @JvmStatic
    fun runAsync(runnable: Runnable, delay: Long) {
        if (isFolia()) {
            plugin.server.asyncScheduler.runDelayed(plugin, { runnable.run() }, delay * 50, TimeUnit.MILLISECONDS)
        } else {
            plugin.server.scheduler.runTaskLaterAsynchronously(plugin, runnable, delay)
        }
    }

    @JvmStatic
    fun runAsync(runnable: Runnable, delay: Long, period: Long) {
        if (isFolia()) {
            plugin.server.asyncScheduler.runAtFixedRate(plugin, { runnable.run() }, delay * 50, period * 50, TimeUnit.MILLISECONDS)
        } else {
            plugin.server.scheduler.runTaskTimerAsynchronously(plugin, runnable, delay, period)
        }
    }

    @JvmStatic
    fun runEAsync(runnable: Runnable): Future<*> {
        return asyncExecutor.submit(runnable)
    }

    @JvmStatic
    fun registerListener(listener: Listener) {
        plugin.server.pluginManager.registerEvents(listener, plugin)
    }

    @JvmStatic
    fun registerSuspendingListener(listener: Listener) {
        plugin.server.pluginManager.registerSuspendingEvents(listener, plugin)
    }

    @JvmStatic
    fun unregisterListener(listener: Listener) {
        HandlerList.unregisterAll(listener)
    }

    @JvmStatic
    fun unregisterAllListeners() {
        HandlerList.unregisterAll(plugin)
    }

    @JvmStatic
    fun hasPlugin(name: String): Boolean {
        return plugin.server.pluginManager.getPlugin(name) != null
    }

    @JvmStatic
    fun hasPlugins(vararg name: String): Boolean {
        return name.all { hasPlugin(it) }
    }

    @JvmStatic
    fun plugin() = plugin

    @JvmStatic
    fun server() = plugin.server

    @JvmStatic
    fun pluginDirectory() = plugin.dataFolder

    @JvmStatic
    fun onlinePlayers(): MutableCollection<out Player> = plugin.server.onlinePlayers

    @JvmStatic
    fun shutdown() {
        unregisterAllListeners()
        asyncExecutor.shutdown()
    }

}

fun dispatcher(async: Boolean = false): CoroutineContext {
    return if (StickyNote.isFolia()) {
        FoliaDispatcher.get(async)
    } else {
        BukkitDispatcher.get(async)
    }
}

fun launch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    plugin.launch(dispatcher(), start, block)
}

suspend fun <T> sync(scope: suspend CoroutineScope.() -> T): T {
    return withContext(dispatcher(), scope)
}

suspend fun <T> deferredAsync(scope: suspend CoroutineScope.() -> T): Deferred<T> {
    return async {
        async {
            scope()
        }
    }
}

suspend fun <T> async(scope: suspend CoroutineScope.() -> T): T {
    return withContext(dispatcher(true), scope)
}

fun runSync(runnable: Runnable) {
    StickyNote.runSync(runnable)
}

fun runSync(runnable: Runnable, delay: Long) {
    StickyNote.runSync(runnable, delay)
}

fun runSync(runnable: Runnable, delay: Long, period: Long) {
    StickyNote.runSync(runnable, delay, period)
}

fun runAsync(runnable: Runnable) {
    StickyNote.runAsync(runnable)
}

fun runAsync(runnable: Runnable, delay: Long) {
    StickyNote.runAsync(runnable, delay)
}

fun runAsync(runnable: Runnable, delay: Long, period: Long) {
    StickyNote.runAsync(runnable, delay, period)
}

fun runEAsync(runnable: Runnable): Future<*> {
    return StickyNote.runEAsync(runnable)
}

fun registerListener(listener: Listener) {
    StickyNote.registerListener(listener)
}

fun registerSuspendingListener(listener: Listener) {
    StickyNote.registerSuspendingListener(listener)
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

val server = StickyNote.server()
val pluginDirectory = StickyNote.pluginDirectory()
val onlinePlayers = StickyNote.onlinePlayers()