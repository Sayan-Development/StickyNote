package org.sayandev.stickynote.core.database.mysql

import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.sayandev.stickynote.core.database.Query
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ThreadFactory

class MySQLDatabase(credentials: MySQLCredentials, poolingSize: Int) : MySQLExecutor(credentials, poolingSize, THREAD_FACTORY) {

    override fun connect() {
        super.connect("com.mysql.cj.jdbc.Driver")
        startQueue()
    }

    override fun scheduleShutdown(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        Thread {
            while (!isQueueEmpty) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            shutdown()
            future.complete(null)
        }.start()
        return future
    }

    override fun shutdown() {
        queue.clear()
        hikari?.close()
    }

    private fun startQueue() {
        Thread {
            while (!isQueueEmpty) {
                if (poolingUsed <= poolingSize) {
                    tick()
                    try {
                        Thread.sleep(1)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onQueryFail(query: Query) {
        //ignored
    }

    override fun onQueryRemoveDueToFail(query: Query) {
        //ignored
    }

    companion object {
        private val THREAD_FACTORY: ThreadFactory = ThreadFactoryBuilder().setNameFormat("mysql-thread-%d").build()
    }
}
