package org.sayandev.stickynote.core.database

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.logging.Logger

class Query private constructor(val statement: String) {
    private val statementValues: MutableMap<Int, Any> = HashMap()
    private val requirements: MutableSet<Query> = HashSet()
    val completableFuture: CompletableFuture<QueryResult> = CompletableFuture()
    private var consumer: Consumer<QueryResult>? = null
    var failedAttempts: Int = 0
        private set

    var statusCode: StatusCode = StatusCode.NOT_STARTED

    fun addRequirement(query: Query): Query {
        requirements.add(query)
        return this
    }

    fun getRequirements(): Set<Query> {
        return requirements
    }

    /**
     * @apiNote internal
     */
    fun complete(result: QueryResult) {
        completableFuture.complete(result)
        if (consumer != null) {
            consumer!!.accept(result)
        }
    }

    fun onComplete(consumer: Consumer<QueryResult>?) {
        this.consumer = consumer
    }

    fun hasDoneRequirements(): Boolean {
        var hasDoneRequirements = true
        for (query in requirements) {
            if (query.statusCode != StatusCode.FINISHED) {
                hasDoneRequirements = false
                break
            }
        }
        return hasDoneRequirements
    }

    fun increaseFailedAttempts() {
        failedAttempts += 1
    }

    fun setStatementValue(index: Int, value: Any): Query {
        statementValues[index] = value
        return this
    }

    fun createPreparedStatement(connection: Connection?): PreparedStatement {
        val preparedStatement = connection?.prepareStatement(statement) ?: throw NullPointerException("Can't prepare statement while connection is null")

        for (index in statementValues.keys) {
            val value = statementValues[index]

            preparedStatement.setObject(index, value)
        }

        return preparedStatement
    }

    enum class StatusCode {
        NOT_STARTED,
        RUNNING,
        FAILED,
        FINISHED
    }

    companion object {
        @JvmStatic
        fun query(statement: String): Query {
            return Query(statement)
        }
    }
}
