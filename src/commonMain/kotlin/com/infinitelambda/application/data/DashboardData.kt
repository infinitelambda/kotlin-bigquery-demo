package com.infinitelambda.application.data

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class DashboardData(
    val favouriteFood: FavouriteFoodData,
    val kotlinInterestLevel: KotlinInterestLevelData,
    val sentiment: SentimentData
)

@Serializable
data class FavouriteFoodData(
    val pizza: Count,
    val burger: Count
)

@Serializable
data class KotlinInterestLevelData(
    val high: Count,
    val medium: Count,
    val low: Count
)

@Serializable
data class SentimentData(
    val positive: Count,
    val negative: Count
)

@JvmInline
@Serializable
value class Count(val value: Long)

fun Long.asCount(): Count =
    Count(this)
