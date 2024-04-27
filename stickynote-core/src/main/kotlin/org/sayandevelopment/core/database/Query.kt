package org.sayandevelopment.core.database

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class Query private constructor(val statement: String) {
    private val statementValues: MutableMap<Int, Any> = HashMap()
    private val requirements: MutableSet<Query> = HashSet()
    val completableFuture: CompletableFuture<ResultSet> = CompletableFuture()
    private var consumer: Consumer<ResultSet>? = null
    var failedAttempts: Int = 0
        private set

    var statusCode: Int = StatusCode.NOT_STARTED.code

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
    fun complete(result: ResultSet) {
        completableFuture.complete(result)
        if (consumer != null) {
            consumer!!.accept(result)
        }
    }

    fun onComplete(consumer: Consumer<ResultSet>?) {
        this.consumer = consumer
    }

    fun hasDoneRequirements(): Boolean {
        var hasDoneRequirements = true
        for (query in requirements) {
            if (query.statusCode != StatusCode.FINISHED.code) {
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

    enum class StatusCode(val code: Int) {
        NOT_STARTED(-1),
        RUNNING(0),
        FAILED(1),
        FINISHED(2)
    }

    companion object {
        fun query(statement: String): Query {
            return Query(statement)
        }
    }
}
