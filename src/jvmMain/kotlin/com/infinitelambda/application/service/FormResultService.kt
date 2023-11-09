package com.infinitelambda.application.service

import arrow.core.Either
import com.infinitelambda.application.FormResultError
import com.infinitelambda.application.FormResultErrors
import com.infinitelambda.application.FormResultNotPersisted
import com.infinitelambda.application.bigQuery.BigQueryWriteStream
import com.infinitelambda.application.data.FormResult
import com.infinitelambda.application.data.asJSONObject
import org.json.JSONArray

object FormResultService {

    context(BigQueryWriteStream, FormResultErrors)
    suspend fun save(result: FormResult) =
        Either.catch {
            val data = JSONArray().apply { put(result.asJSONObject()) }
            push(data)
        }.mapLeft {
            FormResultNotPersisted("Failed to persist form result", it)
        }.bind()


}