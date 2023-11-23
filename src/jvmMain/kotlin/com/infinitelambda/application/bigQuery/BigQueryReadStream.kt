package com.infinitelambda.application.bigQuery

import com.google.cloud.bigquery.BigQuery
import com.google.cloud.bigquery.BigQueryOptions
import com.google.cloud.bigquery.FieldValueList
import com.google.cloud.bigquery.QueryJobConfiguration
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.minutes

class BigQueryReadStream(private val config: BigQueryConfig) : AutoCloseable {

    private val bigQuery: BigQuery = BigQueryOptions.getDefaultInstance().service

    fun <T> query(projection: List<String>, mapper: (FieldValueList) -> T) = flow {
        var isActiveSubscription = true
        val query = createQuery(projection)
        while (isActiveSubscription) {
            val queryResult = executeQuery(query)
            emit(queryResult.map(mapper).toList())

            delay(1.minutes)

            isActiveSubscription = currentCoroutineContext().isActive
        }
    }

    private fun createQuery(projection: List<String>): String =
        "select ${projection.joinToString(",")} from `${config.projectId.value}.${config.datasetName.value}.${config.tableName.value}`"

    private fun executeQuery(query: String): Sequence<FieldValueList> {
        val queryConfig = QueryJobConfiguration.newBuilder(query).build()
        val result = bigQuery.query(queryConfig)
        return result.iterateAll().iterator().asSequence()
    }

    override fun close() {

    }

}