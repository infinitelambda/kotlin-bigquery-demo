package com.infinitelambda.application.service

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import com.google.cloud.language.v2.Document
import com.google.cloud.language.v2.LanguageServiceClient
import com.infinitelambda.application.EnrichmentFailed
import com.infinitelambda.application.FormResultErrors
import com.infinitelambda.application.FormResultNotPersisted
import com.infinitelambda.application.bigQuery.BigQueryWriteStream
import com.infinitelambda.application.data.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.Clock
import java.time.Instant
import java.time.format.DateTimeFormatter

data class EnrichedFormResult(
    val favouriteFood: FavouriteFood,
    val kotlinInterestLevel: KotlinInterestLevel,
    val comment: Comment,
    val sentiment: Sentiment
) {

    fun asJSONObject(): JSONObject =
        JSONObject().apply {
            put("favourite_food", favouriteFood.name)
            put("kotlin_interest_level", kotlinInterestLevel.name)
            put("comment", comment.value)
            put("sentiment", sentiment.name)
            put("submission_time", DateTimeFormatter.ISO_INSTANT.format(Instant.now(Clock.systemUTC())))
        }

}

enum class Sentiment {
    POSITIVE, NEGATIVE
}

object FormResultService {

    context(BigQueryWriteStream, LanguageServiceClient, FormResultErrors)
    suspend fun save(result: FormResult) {
        val enriched = enrich(result)
        persist(enriched)
    }

    context(LanguageServiceClient, FormResultErrors)
    private suspend fun enrich(original: FormResult): EnrichedFormResult {
        val document = Document.newBuilder().setType(Document.Type.PLAIN_TEXT)
            .setContent(original.comment.value)
            .build()
        val sentimentResponse = analyzeSentiment(document)
        return sentimentResponse?.let {
            EnrichedFormResult(
                favouriteFood = original.favouriteFood,
                kotlinInterestLevel = original.kotlinInterestLevel,
                comment = original.comment,
                sentiment = if (it.documentSentiment.score > 0) { Sentiment.POSITIVE } else { Sentiment.NEGATIVE }
            )
        } ?: raise(EnrichmentFailed("Could not analyze comment sentiment"))
    }

    context(BigQueryWriteStream, FormResultErrors)
    private suspend fun persist(result: EnrichedFormResult) =
        Either.catch {
            val data = JSONArray().apply { put(result.asJSONObject()) }
            push(data)
        }.mapLeft {
            FormResultNotPersisted("Failed to persist form result", it)
        }.bind()
}