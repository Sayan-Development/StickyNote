package org.sayandev.stickynote.core.database

import java.util.*
import java.util.concurrent.CompletableFuture

abstract class Database protected constructor() {
    protected val queue = EnumMap<Priority, MutableList<Query>>(Priority::class.java)

    /**
     * If a query fails more than this value, It will be removed from the queue.
     */
    protected var failAttemptRemoval: Int = 2

    init {
        for (priority in Priority.entries) {
            queue[priority] = ArrayList()
        }
    }

    /**
     * Initializes the database connection.
     */
    abstract fun connect()

    /**
     * Shutdowns the database once queue becomes empty.
     * @return A completableFuture that will be completed once database shutdowns successfully.
     */
    abstract fun scheduleShutdown(): CompletableFuture<Void>

    /**
     * Force shutdowns the database and clears the queue.
     */
    abstract fun shutdown()

    /**
     * Queues a query.
     * @param query Statement that is going to run.
     * @param priority Priority of the query in queue. Higher priorities will be run sooner in the queue.
     * @return Query class that contains CompletableFuture with ResultSet callback. Useful when you need the results of a query.
     * @see Query
     */
    fun queueQuery(query: Query, priority: Priority): Query {
        queue[priority]!!.add(query)
        return query
    }

    /**
     * Queues a query with normal priority.
     * @param query Statement that is going to run.
     * @return Query class that contains CompletableFuture with ResultSet callback. Useful when you need the results of a query.
     * @see Query
     */
    fun queueQuery(query: Query): Query {
        queue[Priority.NORMAL]!!.add(query)
        return query
    }

    abstract fun runQuery(query: Query): QueryResult

    val isQueueEmpty: Boolean
        /**
         * Returns whether queue is empty or not.
         * @return true if queue is empty
         */
        get() = queue.values.isEmpty()
}
