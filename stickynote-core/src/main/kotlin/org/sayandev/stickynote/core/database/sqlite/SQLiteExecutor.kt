package org.sayandev.stickynote.core.database.sqlite

import org.sayandev.stickynote.core.database.Database
import org.sayandev.stickynote.core.database.Priority
import org.sayandev.stickynote.core.database.Query
import org.sayandev.stickynote.core.database.QueryResult
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.util.logging.Logger

abstract class SQLiteExecutor protected constructor(protected val dbFile: File, private val logger: Logger) : Database() {
    protected var connection: Connection? = null

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
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.path)
        } catch (e: SQLException) {
            logger.severe(e.message)
            e.printStackTrace()
        }
    }

    override fun runQuery(query: Query): QueryResult {
        return executeQuerySync(query)
    }

    fun executeQuerySync(query: Query): QueryResult {
        try {
            val preparedStatement = query.createPreparedStatement(connection)
            var resultSet: ResultSet? = null

            if (query.statement.startsWith("INSERT") ||
                query.statement.startsWith("UPDATE") ||
                query.statement.startsWith("DELETE") ||
                query.statement.startsWith("CREATE") ||
                query.statement.startsWith("ALTER")
            ) {
                preparedStatement.executeUpdate()
                preparedStatement.close()
            }
            else resultSet = preparedStatement.executeQuery()

            if (resultSet != null) {
                query.complete(resultSet)
            }

            return QueryResult(Query.StatusCode.FINISHED, resultSet)
        } catch (e: SQLException) {
            onQueryFail(query)
            e.printStackTrace()

            query.increaseFailedAttempts()
            if (query.failedAttempts > failAttemptRemoval) {
                onQueryRemoveDueToFail(query)
                return QueryResult(Query.StatusCode.FINISHED, null)
            }
        }
        return QueryResult(Query.StatusCode.FAILED, null)
    }

    protected fun tick() {
        for (priority in Priority.entries) {
            val queries = queue[priority] ?: continue
            val query = queries.firstOrNull() ?: continue

            if (executeQuerySync(query).statusCode == Query.StatusCode.FINISHED) {
                queries.removeFirstOrNull()
            }
            break
        }
    }

    protected abstract fun onQueryFail(query: Query)

    protected abstract fun onQueryRemoveDueToFail(query: Query)

}
