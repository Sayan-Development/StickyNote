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
import java.util.concurrent.*
import kotlin.math.max

abstract class MySQLExecutor(
    private val credentials: MySQLCredentials,
    protected val poolingSize: Int,
    threadFactory: ThreadFactory,
    val verifyCertificate: Boolean,
    val keepaliveTime: Long?,
    val connectionTimeout: Long?,
    val minimumIdle: Int?,
    val maxLifeTime: Long?,
    val allowPublicKeyRetrieval: Boolean
) : Database() {

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(max(1, poolingSize), threadFactory)

    protected lateinit var hikari: HikariDataSource
    protected var poolingUsed: Int = 0

    protected fun connect(driverClassName: String?) {
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = credentials.url
        if (driverClassName != null) {
            hikariConfig.driverClassName = driverClassName
        }
        hikariConfig.username = credentials.username
        hikariConfig.password = credentials.password
        hikariConfig.maximumPoolSize = poolingSize
        hikariConfig.minimumIdle = minimumIdle ?: hikari.maximumPoolSize
        if (keepaliveTime != null) {
            hikariConfig.keepaliveTime = keepaliveTime
        }
        if (connectionTimeout != null) {
            hikariConfig.connectionTimeout = connectionTimeout
        }
        hikariConfig.maxLifetime = maxLifeTime ?: 1800000

        hikariConfig.addDataSourceProperty("socketTimeout", TimeUnit.SECONDS.toMillis(30).toString())
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true")
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250")
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true")
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true")
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true")
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true")
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true")
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true")
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false")
        hikariConfig.addDataSourceProperty("alwaysSendSetIsolation", "false")
        hikariConfig.addDataSourceProperty("cacheCallableStmts", "true")
        hikariConfig.addDataSourceProperty("allowPublicKeyRetrieval", allowPublicKeyRetrieval.toString())
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8")

        this.hikari = HikariDataSource(hikariConfig)
    }

    protected fun tick() {
        for (priority in Priority.entries) {
            val queries = queue[priority]?.toMutableList() ?: continue
            if (queries.isEmpty()) continue

            val removedQueries: MutableSet<Query> = HashSet()
            for (query in queries.filter { it.statusCode == StatusCode.FINISHED }.toList()) {
                removedQueries.add(query)
            }
            queries.removeAll(removedQueries)
            queue[priority]!!.removeAll(removedQueries)

            for (query in queries) {
                if (query.hasDoneRequirements() && query.statusCode != StatusCode.RUNNING) {
                    query.statusCode = StatusCode.RUNNING

                    executeQuery(query).whenComplete { statusCode: StatusCode, error: Throwable? ->
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
        getConnection().use { connection ->
            try {
                val preparedStatement = query.createPreparedStatement(connection)

                val isUpdate = query.statement.startsWith("INSERT") ||
                        query.statement.startsWith("UPDATE") ||
                        query.statement.startsWith("DELETE") ||
                        query.statement.startsWith("CREATE") ||
                        query.statement.startsWith("ALTER")
                var resultSet: ResultSet? = null

                if (isUpdate) {
                    preparedStatement.closeOnCompletion()
                    preparedStatement.executeUpdate()
                    preparedStatement.close()
                }
                else {
                    resultSet = preparedStatement.executeQuery()
                }

                if (resultSet != null) {
                    query.complete(resultSet)
                }

                return QueryResult(StatusCode.FINISHED, resultSet)
            } catch (e: SQLException) {
                onQueryFail(query)
                e.printStackTrace()

                query.increaseFailedAttempts()
                if (query.failedAttempts > failAttemptRemoval) {
                    onQueryRemoveDueToFail(query)

                    return QueryResult(StatusCode.FINISHED, null)
                }


                return QueryResult(StatusCode.FAILED, null)
            }
        }
    }

    private fun executeQuery(query: Query): CompletableFuture<StatusCode> {
        val completableFuture = CompletableFuture<StatusCode>()

        val runnable = Runnable { completableFuture.complete(executeQuerySync(query).statusCode) }

        threadPool.submit(runnable)

        return completableFuture
    }

    private fun getConnection(): Connection {
        return hikari.connection ?: throw NullPointerException("Can't create connection while HikariDataSource (hikari) is null")
    }

    private fun closeConnection(connection: Connection) {
        connection.close()
    }

    protected abstract fun onQueryFail(query: Query)

    protected abstract fun onQueryRemoveDueToFail(query: Query)
}
