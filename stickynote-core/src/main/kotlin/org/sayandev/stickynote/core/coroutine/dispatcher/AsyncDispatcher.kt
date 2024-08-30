package org.sayandev.stickynote.core.coroutine.dispatcher

import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class AsyncDispatcher(
    threadPrefix: String,
    threads: Int
): CoroutineDispatcher() {

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(
        threads.coerceAtLeast(1),
        ThreadFactoryBuilder().setNameFormat("$threadPrefix-%d").build()
    )

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        threadPool.submit(block)
    }

}