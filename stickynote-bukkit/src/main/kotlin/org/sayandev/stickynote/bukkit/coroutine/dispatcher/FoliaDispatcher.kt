package org.sayandev.stickynote.bukkit.coroutine.dispatcher

import com.github.shynixn.mccoroutine.folia.asyncDispatcher
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import kotlinx.coroutines.Dispatchers
import org.sayandev.stickynote.bukkit.plugin
import kotlin.coroutines.CoroutineContext

object FoliaDispatcher {

    fun get(async: Boolean = false): CoroutineContext {
        return if (async) {
            plugin.asyncDispatcher
        } else {
            plugin.globalRegionDispatcher
        }
    }
}

fun Dispatchers.folia(async: Boolean = false): CoroutineContext {
    return FoliaDispatcher.get(async)
}