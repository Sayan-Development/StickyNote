package org.sayandev.stickynote.core.database

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class QueryResult(val statusCode: Query.StatusCode, private val resultSet: ResultSet?) : AutoCloseable {
    private val connection: Connection? = resultSet?.statement?.connection

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