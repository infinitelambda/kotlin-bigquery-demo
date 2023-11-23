package com.infinitelambda.application.environment

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.autoCloseable
import arrow.fx.coroutines.continuations.resource
import com.google.cloud.language.v2.LanguageServiceClient
import com.infinitelambda.application.bigQuery.*

class Dependencies(
    val writeStream: BigQueryWriteStream,
    val readStream: BigQueryReadStream,
    val languageServiceClient: LanguageServiceClient
)


@Suppress("FunctionName")
fun Dependencies(env: Environment): Resource<Dependencies> = resource {
    val writeStream = autoCloseable {
        BigQueryWriteStream(
            env.bigQuery.projectId,
            env.bigQuery.datasetName,
            env.bigQuery.resultsTableName
        )
    }
    val readStream = autoCloseable {
        BigQueryReadStream(
            config = BigQueryConfig(
                ProjectId(env.bigQuery.projectId),
                DatasetName(env.bigQuery.datasetName),
                TableName(env.bigQuery.aggregatedResultsViewName)
            )
        )
    }
    val languageServiceClient = autoCloseable { LanguageServiceClient.create() }

    Dependencies(writeStream, readStream, languageServiceClient)
}