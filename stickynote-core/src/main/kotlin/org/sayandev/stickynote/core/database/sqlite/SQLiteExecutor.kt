package org.sayandev.stickynote.core.database.sqlite

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.sayandev.stickynote.core.database.Database
import org.sayandev.stickynote.core.database.Priority
import org.sayandev.stickynote.core.database.Query
import org.sayandev.stickynote.core.database.Query.StatusCode
import org.sayandev.stickynote.core.database.QueryResult
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

abstract class SQLiteExecutor protected constructor(protected val dbFile: File, private val logger: Logger, val maxConnectionPool: Int = 5) : Database() {

    private var dataSource: HikariDataSource? = null
    protected val connectionTimeout = TimeUnit.SECONDS.toMillis(30)

    init {
        try {
            if (!dbFile.exists()) {
                dbFile.parentFile.mkdirs()
                dbFile.createNewFile()
            }
        } catch (e: IOException) {
            logger.severe("Failed to create the sqlite database file. Stacktrace:")
            e.printStackTrace()
        }
    }

    override fun connect() {
        try {
            if (dataSource == null) {
                val config = HikariConfig()
                config.jdbcUrl = "jdbc:sqlite:${dbFile.path}"
                config.driverClassName = "org.sqlite.JDBC"
                config.connectionTimeout = connectionTimeout
                config.idleTimeout = 5000
                config.maxLifetime = 1800000
                config.maximumPoolSize = maxConnectionPool
                config.minimumIdle = 1
                config.poolName = "stickynote-sqlite-pool"

                config.addDataSourceProperty("socketTimeout", TimeUnit.SECONDS.toMillis(30).toString())
                config.addDataSourceProperty("cachePrepStmts", "true")
                config.addDataSourceProperty("prepStmtCacheSize", "250")
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
                config.addDataSourceProperty("useServerPrepStmts", "true")
                config.addDataSourceProperty("useLocalSessionState", "true")
                config.addDataSourceProperty("rewriteBatchedStatements", "true")
                config.addDataSourceProperty("cacheResultSetMetadata", "true")
                config.addDataSourceProperty("cacheServerConfiguration", "true")
                config.addDataSourceProperty("elideSetAutoCommits", "true")
                config.addDataSourceProperty("maintainTimeStats", "false")
                config.addDataSourceProperty("alwaysSendSetIsolation", "false")
                config.addDataSourceProperty("cacheCallableStmts", "true")
                config.addDataSourceProperty("allowPublicKeyRetrieval", "true")
                config.addDataSourceProperty("characterEncoding", "utf8")

                config.addDataSourceProperty("foreign_keys", "true")
                config.addDataSourceProperty("journal_mode", "WAL")
                config.addDataSourceProperty("synchronous", "NORMAL")

                dataSource = HikariDataSource(config)
                logger.info("Successfully configured SQLite connection pool")
            }
        } catch (e: Exception) {
            logger.severe("Failed to create connection pool: ${e.message}")
            e.printStackTrace()
        }
    }

    protected fun getConnection(): Connection? {
        return try {
            dataSource?.connection
        } catch (e: SQLException) {
            logger.severe("Failed to get connection from pool: ${e.message}")
            e.printStackTrace()
            null
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
                    query.complete(QueryResult(StatusCode.FINISHED, connection, resultSet))
                }

                query.statusCode = StatusCode.FINISHED
                return QueryResult(StatusCode.FINISHED, connection, resultSet)
            } catch (e: SQLException) {
                onQueryFail(query)
                e.printStackTrace()

                query.increaseFailedAttempts()
                if (query.failedAttempts > failAttemptRemoval) {
                    onQueryRemoveDueToFail(query)

                    query.statusCode = StatusCode.FINISHED
                    return QueryResult(StatusCode.FINISHED, connection, null)
                }


                query.statusCode = StatusCode.FAILED
                return QueryResult(StatusCode.FAILED, connection, null)
            }
        }
    }

    protected fun tick() {
        for (priority in Priority.entries) {
            val queries = queue[priority] ?: continue
            val query = queries.firstOrNull() ?: continue

            if (query.hasDoneRequirements() && query.statusCode != StatusCode.RUNNING) {
                query.statusCode = StatusCode.RUNNING
            }

            val queryResult = executeQuerySync(query)
            if (queryResult.statusCode != StatusCode.NOT_STARTED) {
                queries.removeFirstOrNull()
            }
            break
        }
    }

    override fun shutdown() {
        dataSource?.close()
    }

    protected abstract fun onQueryFail(query: Query)

    protected abstract fun onQueryRemoveDueToFail(query: Query)
}