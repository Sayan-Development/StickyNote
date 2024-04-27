package org.sayandevelopment.core.database.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.sayandevelopment.core.database.Database
import org.sayandevelopment.core.database.Priority
import org.sayandevelopment.core.database.Query
import org.sayandevelopment.core.database.Query.StatusCode
import org.sayandevelopment.core.database.QueryResult
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
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
        val connection = createConnection()
        try {
            val preparedStatement = query.createPreparedStatement(connection)
            var resultSet: ResultSet? = null

            if (query.statement.startsWith("INSERT") ||
                query.statement.startsWith("UPDATE") ||
                query.statement.startsWith("DELETE") ||
                query.statement.startsWith("CREATE") ||
                query.statement.startsWith("ALTER")
            ) preparedStatement.executeUpdate()
            else resultSet = preparedStatement.executeQuery()

            if (resultSet != null) {
                query.complete(resultSet)
            }

            closeConnection(connection)

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
