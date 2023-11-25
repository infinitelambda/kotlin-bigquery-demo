package com.infinitelambda.application.service

import com.google.cloud.bigquery.FieldValueList
import com.infinitelambda.application.bigQuery.BigQueryReadStream
import com.infinitelambda.application.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PIZZA_COUNT = "pizza_count"
private const val BURGER_COUNT = "burger_count"

private const val HIGH_INTEREST_COUNT = "high_interest_count"
private const val MEDIUM_INTEREST_COUNT = "medium_interest_count"
private const val LOW_INTEREST_COUNT = "low_interest_count"

private const val POSITIVE_SENTIMENT_COUNT = "positive_sentiment_count"
private const val NEGATIVE_SENTIMENT_COUNT = "negative_sentiment_count"

object DashboardService {

    context(BigQueryReadStream)
    fun observeDashboardData(): Flow<DashboardData> = query(
        projection = listOf(
            PIZZA_COUNT,
            BURGER_COUNT,
            HIGH_INTEREST_COUNT,
            MEDIUM_INTEREST_COUNT,
            LOW_INTEREST_COUNT,
            POSITIVE_SENTIMENT_COUNT,
            NEGATIVE_SENTIMENT_COUNT
        )
    ) { it.mapToDashboardData() }
        .map { it[0] }

    private fun FieldValueList.mapToDashboardData(): DashboardData =
        DashboardData(
            favouriteFood = FavouriteFoodData(
                pizza = get(PIZZA_COUNT).longValue.asCount(),
                burger = get(BURGER_COUNT).longValue.asCount()
            ),
            kotlinInterestLevel = KotlinInterestLevelData(
                high = get(HIGH_INTEREST_COUNT).longValue.asCount(),
                medium = get(MEDIUM_INTEREST_COUNT).longValue.asCount(),
                low = get(LOW_INTEREST_COUNT).longValue.asCount(),
            ),
            sentiment = SentimentData(
                positive = get(POSITIVE_SENTIMENT_COUNT).longValue.asCount(),
                negative = get(NEGATIVE_SENTIMENT_COUNT).longValue.asCount()
            )
        )

}