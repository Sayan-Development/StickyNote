package org.sayandev.stickynote.core.database

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class QueryResult(val statusCode: Query.StatusCode, val connection: Connection?, private val resultSet: ResultSet?) : AutoCloseable {

    fun getResult(): ResultSet? = resultSet

    override fun close() {
        try {
            resultSet?.close()
            resultSet?.statement?.close()
            connection?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}