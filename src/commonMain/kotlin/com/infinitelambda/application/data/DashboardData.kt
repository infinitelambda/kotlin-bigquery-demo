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
    val pizza: Percentage,
    val burger: Percentage
)

@Serializable
data class KotlinInterestLevelData(
    val high: Percentage,
    val medium: Percentage,
    val low: Percentage
)

@Serializable
data class SentimentData(
    val positive: Percentage,
    val negative: Percentage
)

@JvmInline
@Serializable
value class Percentage(val value: Double)

fun Double.asPercentage(): Percentage =
    Percentage(this)
