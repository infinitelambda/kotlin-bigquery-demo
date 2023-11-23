package com.infinitelambda.application.service

import com.google.cloud.bigquery.FieldValueList
import com.infinitelambda.application.bigQuery.BigQueryReadStream
import com.infinitelambda.application.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PIZZA_PCT = "pizza_pct"
private const val BURGER_PCT = "burger_pct"

private const val HIGH_INTEREST_PCT = "high_interest_pct"
private const val MEDIUM_INTEREST_PCT = "medium_interest_pct"
private const val LOW_INTEREST_PCT = "low_interest_pct"

private const val POSITIVE_SENTIMENT_PCT = "positive_sentiment_pct"
private const val NEGATIVE_SENTIMENT_PCT = "negative_sentiment_pct"

object DashboardService {

    context(BigQueryReadStream)
    fun observeDashboardData(): Flow<DashboardData> = query(
        projection = listOf(
            PIZZA_PCT,
            BURGER_PCT,
            HIGH_INTEREST_PCT,
            MEDIUM_INTEREST_PCT,
            LOW_INTEREST_PCT,
            POSITIVE_SENTIMENT_PCT,
            NEGATIVE_SENTIMENT_PCT
        )
    ) { it.mapToDashboardData() }
        .map { it[0] }

    private fun FieldValueList.mapToDashboardData(): DashboardData =
        DashboardData(
            favouriteFood = FavouriteFoodData(
                pizza = get(PIZZA_PCT).doubleValue.asPercentage(),
                burger = get(BURGER_PCT).doubleValue.asPercentage()
            ),
            kotlinInterestLevel = KotlinInterestLevelData(
                high = get(HIGH_INTEREST_PCT).doubleValue.asPercentage(),
                medium = get(MEDIUM_INTEREST_PCT).doubleValue.asPercentage(),
                low = get(LOW_INTEREST_PCT).doubleValue.asPercentage(),
            ),
            sentiment = SentimentData(
                positive = get(POSITIVE_SENTIMENT_PCT).doubleValue.asPercentage(),
                negative = get(NEGATIVE_SENTIMENT_PCT).doubleValue.asPercentage()
            )
        )

}