package org.sayandev.stickynote.core.utils

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

object CoroutineUtils {
    suspend fun <T> Deferred<T>.awaitWithTimeout(timeout: Long, onTimeout: (TimeoutCancellationException) -> Unit = {}): T? {
        return try {
            withTimeout(timeout) {
                await()
            }
        } catch (e: TimeoutCancellationException) {
            onTimeout(e)
            null
        }
    }

    fun launch(
        dispatcher: CoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        val session = CoroutineScope(dispatcher)
        if (!session.isActive) {
            return Job()
        }

        return session.launch(dispatcher, start, block)
    }

    fun <T> async(
        dispatcher: CoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> {
        val session = CoroutineScope(dispatcher)
        if (!session.isActive) {
            return CompletableDeferred<T>().apply { cancel() }
        }

        return session.async(dispatcher, start, block)
    }
}

fun launch(
    dispatcher: CoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return CoroutineUtils.launch(dispatcher, start, block)
}

fun <T> async(
    dispatcher: CoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return CoroutineUtils.async(dispatcher, start, block)
}