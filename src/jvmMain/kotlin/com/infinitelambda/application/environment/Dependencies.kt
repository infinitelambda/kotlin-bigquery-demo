package com.infinitelambda.application.environment

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.autoCloseable
import arrow.fx.coroutines.continuations.resource
import arrow.fx.coroutines.fromCloseable
import com.google.cloud.language.v2.LanguageServiceClient
import com.infinitelambda.application.bigQuery.BigQueryWriteStream

class Dependencies(
    val writeStream: BigQueryWriteStream,
    val languageServiceClient: LanguageServiceClient
)


@Suppress("FunctionName")
fun Dependencies(env: Environment): Resource<Dependencies> = resource {
    val writeStream = autoCloseable { BigQueryWriteStream(env.bigQuery.projectId, env.bigQuery.datasetName, env.bigQuery.tableName) }
    val languageServiceClient = autoCloseable { LanguageServiceClient.create() }

    Dependencies(writeStream, languageServiceClient)
}