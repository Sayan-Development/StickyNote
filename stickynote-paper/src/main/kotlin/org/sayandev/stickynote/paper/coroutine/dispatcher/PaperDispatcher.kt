package org.sayandev.stickynote.paper.coroutine.dispatcher

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import kotlinx.coroutines.Dispatchers
import org.sayandev.stickynote.paper.plugin
import kotlin.coroutines.CoroutineContext

object PaperDispatcher {

    fun get(async: Boolean = false): CoroutineContext {
        return if (async) {
            plugin.asyncDispatcher
        } else {
            plugin.minecraftDispatcher
        }
    }
}

fun Dispatchers.paper(async: Boolean = false): CoroutineContext {
    return PaperDispatcher.get(async)
}
