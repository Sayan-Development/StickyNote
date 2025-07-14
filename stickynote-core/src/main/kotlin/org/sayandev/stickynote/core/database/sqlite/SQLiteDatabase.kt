package org.sayandev.stickynote.core.database.sqlite

import org.sayandev.stickynote.core.database.Query
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

class SQLiteDatabase(dbFile: File, logger: Logger, maxConnectionPool: Int = 5) : SQLiteExecutor(dbFile, logger, maxConnectionPool) {
    override fun connect() {
        super.connect()
        startQueue()
    }

    override fun scheduleShutdown(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        Thread {
            while (!isQueueEmpty) {
                Thread.sleep(1000)
            }
            shutdown()
            future.complete(null)
        }.start()
        return future
    }

    private fun startQueue() {
        Thread {
            while (!isQueueEmpty) {
                tick()
                Thread.sleep(1)
            }
        }.start()
    }

    override fun onQueryFail(query: Query) {
        //ignored
    }

    override fun onQueryRemoveDueToFail(query: Query) {
        //ignored
    }
}