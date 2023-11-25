package com.infinitelambda.application.analisys

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.ensureNotNull
import com.google.cloud.language.v2.AnalyzeSentimentResponse
import com.google.cloud.language.v2.Document
import com.google.cloud.language.v2.LanguageServiceClient
import com.infinitelambda.application.EnrichmentFailed
import com.infinitelambda.application.data.Comment

enum class Sentiment {
    POSITIVE, NEGATIVE
}

class SentimentAnalyser : AutoCloseable {

    private val languageServiceClient = LanguageServiceClient.create()

    context(Raise<EnrichmentFailed>)
    fun analyseSentiment(input: Comment): Sentiment = catch<Sentiment>(
        {
            val response = languageServiceClient.analyzeSentiment(input.toDocument())
            ensureNotNull(response) { EnrichmentFailed("Could analyse comment sentiment") }
                .toSentiment()
        }
    ) { raise(EnrichmentFailed("Could analyse comment sentiment. Cause: ${it.message}")) }

    override fun close() {
        languageServiceClient.close()
    }
}

private fun Comment.toDocument() =
    Document.newBuilder()
        .setType(Document.Type.PLAIN_TEXT)
        .setContent(value)
        .build()

private fun AnalyzeSentimentResponse.toSentiment() =
    if (documentSentiment.score > 0) {
        Sentiment.POSITIVE
    } else {
        Sentiment.NEGATIVE
    }
