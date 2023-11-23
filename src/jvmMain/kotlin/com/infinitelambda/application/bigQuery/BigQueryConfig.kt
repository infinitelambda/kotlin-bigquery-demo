package com.infinitelambda.application.bigQuery

data class BigQueryConfig(
    val projectId: ProjectId,
    val datasetName: DatasetName,
    val tableName: TableName
)

@JvmInline
value class ProjectId(val value: String)

@JvmInline
value class DatasetName(val value: String)

@JvmInline
value class TableName(val value: String)