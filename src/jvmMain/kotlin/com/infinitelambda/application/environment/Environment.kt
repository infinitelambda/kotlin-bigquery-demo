package com.infinitelambda.application.environment

import java.lang.System.getenv

private const val DEFAULT_PORT: Int = 8080
private const val DEFAULT_HOST: String = "0.0.0.0"

data class Environment(
    val http: Http = Http(),
    val bigQuery: BigQuery = BigQuery()
)

data class Http(
    val host: String = getenv("HOST") ?: DEFAULT_HOST,
    val port: Int = getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
)

data class BigQuery(
    val projectId: String = getenvOrThrow("PROJECT_ID"),
    val datasetName: String = getenvOrThrow("DATASET_NAME"),
    val resultsTableName: String = getenvOrThrow("RESULTS_TABLE_NAME"),
    val aggregatedResultsViewName: String = getenvOrThrow("AGG_RESULTS_VIEW_NAME")
)

private fun getenvOrThrow(name: String) =
    getenv(name) ?: throw IllegalStateException("No value provided for $name env variable")