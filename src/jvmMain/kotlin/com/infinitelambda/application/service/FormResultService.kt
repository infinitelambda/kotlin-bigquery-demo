package com.infinitelambda.application.service

import arrow.core.raise.catch
import com.infinitelambda.application.FormResultErrors
import com.infinitelambda.application.FormResultNotPersisted
import com.infinitelambda.application.analisys.Sentiment
import com.infinitelambda.application.analisys.SentimentAnalyser
import com.infinitelambda.application.bigQuery.BigQueryWriteStream
import com.infinitelambda.application.data.Comment
import com.infinitelambda.application.data.FavouriteFood
import com.infinitelambda.application.data.FormResult
import com.infinitelambda.application.data.KotlinInterestLevel
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

object FormResultService {

    context(BigQueryWriteStream, SentimentAnalyser, FormResultErrors)
    suspend fun save(result: FormResult) =
        result.enrichWith(analyseSentiment(result.comment))
            .persist()

}

private fun FormResult.enrichWith(sentiment: Sentiment) =
    EnrichedFormResult(
        favouriteFood = favouriteFood,
        kotlinInterestLevel = kotlinInterestLevel,
        comment = comment,
        sentiment = sentiment
    )

context(BigQueryWriteStream, FormResultErrors)
private suspend fun EnrichedFormResult.persist(): Unit = catch(
    {
        val data = JSONArray().apply { put(this@persist.asJSONObject()) }
        push(data)
    }
) {
    FormResultNotPersisted("Failed to persist form result", it)
}