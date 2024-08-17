package org.sayandev.stickynote.bukkit.coroutine.dispatcher

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import org.sayandev.stickynote.bukkit.plugin
import kotlin.coroutines.CoroutineContext

object BukkitDispatcher {

    fun get(async: Boolean = false): CoroutineContext {
        return if (async) {
            plugin.asyncDispatcher
        } else {
            plugin.minecraftDispatcher
        }
    }
}