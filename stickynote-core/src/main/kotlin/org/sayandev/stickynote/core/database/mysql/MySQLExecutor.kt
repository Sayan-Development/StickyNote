package org.sayandev.stickynote.core.database.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.sayandev.stickynote.core.database.Database
import org.sayandev.stickynote.core.database.Priority
import org.sayandev.stickynote.core.database.Query
import org.sayandev.stickynote.core.database.Query.StatusCode
import org.sayandev.stickynote.core.database.QueryResult
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.logging.Logger
import kotlin.math.max

abstract class MySQLExecutor(
    private val credentials: MySQLCredentials,
    protected val poolingSize: Int,
    threadFactory: ThreadFactory
) : Database() {

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(max(1, poolingSize), threadFactory)

    protected var hikari: HikariDataSource? = null
    protected var poolingUsed: Int = 0

    protected fun connect(driverClassName: String) {
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = credentials.url
        hikariConfig.driverClassName = driverClassName
        hikariConfig.username = credentials.username
        hikariConfig.password = credentials.password
        hikariConfig.maximumPoolSize = poolingSize

        this.hikari = HikariDataSource(hikariConfig)
    }

    protected fun tick() {
        for (priority in Priority.entries) {
            val queries = queue[priority]?.toMutableList() ?: continue
            if (queries.isEmpty()) continue

            val removedQueries: MutableSet<Query> = HashSet()
            for (query in queries) {
                if (query.statusCode == StatusCode.FINISHED.code) removedQueries.add(query)
            }
            queries.removeAll(removedQueries)
            queue[priority]!!.removeAll(removedQueries)

            for (query in queries) {
                if (query.hasDoneRequirements() && query.statusCode != StatusCode.RUNNING.code) {
                    query.statusCode = StatusCode.RUNNING.code

                    executeQuery(query).whenComplete { statusCode: Int, error: Throwable? ->
                        error?.printStackTrace()

                        query.statusCode = statusCode
                        poolingUsed--
                    }

                    poolingUsed++
                    if (poolingUsed >= poolingSize) break
                }
            }
            if (poolingUsed >= poolingSize) break
            if (queries.isNotEmpty()) break
        }
    }

    override fun runQuery(query: Query): QueryResult {
        return executeQuerySync(query)
    }

    private fun executeQuerySync(query: Query): QueryResult {
        Logger.getGlobal().warning("Creating connection.")
        val connection = createConnection()
        Logger.getGlobal().warning("Created connection: ${connection}")
        try {
            Logger.getGlobal().warning("Creating prepared statement")
            val preparedStatement = query.createPreparedStatement(hikari?.connection)
            Logger.getGlobal().warning("Created prepared statement: ${preparedStatement}")
            var resultSet: ResultSet? = null

            if (query.statement.startsWith("INSERT") ||
                query.statement.startsWith("UPDATE") ||
                query.statement.startsWith("DELETE") ||
                query.statement.startsWith("CREATE") ||
                query.statement.startsWith("ALTER")
            ) {
                Logger.getGlobal().warning("Executing update")
                preparedStatement.executeUpdate()
                Logger.getGlobal().warning("Executed update")
                Logger.getGlobal().warning("Closing statement")
                preparedStatement.close()
                Logger.getGlobal().warning("Closed statement")
            }
            else resultSet = preparedStatement.executeQuery()
            Logger.getGlobal().warning("Executed query: ${resultSet}")

            if (resultSet != null) {
                Logger.getGlobal().warning("Completing result")
                query.complete(resultSet)
                Logger.getGlobal().warning("Completed result: ${resultSet}")
            }

            Logger.getGlobal().warning("Closing connection")
            closeConnection(connection)
            Logger.getGlobal().warning("Closed connection")

            return QueryResult(StatusCode.FINISHED, resultSet)
        } catch (e: SQLException) {
            onQueryFail(query)
            e.printStackTrace()

            query.increaseFailedAttempts()
            if (query.failedAttempts > failAttemptRemoval) {
                closeConnection(connection)
                onQueryRemoveDueToFail(query)

                return QueryResult(StatusCode.FINISHED, null)
            }

            closeConnection(connection)

            return QueryResult(StatusCode.FAILED, null)
        }
    }

    private fun executeQuery(query: Query): CompletableFuture<Int> {
        val completableFuture = CompletableFuture<Int>()

        val runnable = Runnable { completableFuture.complete(executeQuerySync(query).statusCode.code) }

        threadPool.submit(runnable)

        return completableFuture
    }

    private fun createConnection(): Connection {
        return hikari?.connection ?: throw NullPointerException("Can't create connection while HikariDataSource (hikari) is null")
    }

    private fun closeConnection(connection: Connection) {
        connection.close()
    }

    protected abstract fun onQueryFail(query: Query)

    protected abstract fun onQueryRemoveDueToFail(query: Query)
}
