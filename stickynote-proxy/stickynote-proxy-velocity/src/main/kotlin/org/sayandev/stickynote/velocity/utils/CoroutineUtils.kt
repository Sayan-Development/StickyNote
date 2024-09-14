package org.sayandev.stickynote.velocity.utils

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

object CoroutineUtils {
    suspend fun <T> CompletableDeferred<T>.awaitWithTimeout(timeout: Long, onTimeout: (TimeoutCancellationException) -> Unit): T? {
        return try {
            withTimeout(timeout) {
                await()
            }
        } catch (e: TimeoutCancellationException) {
            onTimeout(e)
            null
        }
    }
}