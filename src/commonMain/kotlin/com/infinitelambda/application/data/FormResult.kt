package com.infinitelambda.application.data

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class FormResult(
    val favouriteFood: FavouriteFood,
    val kotlinInterestLevel: KotlinInterestLevel,
    val comment: Comment
)

enum class FavouriteFood {
    BURGER, PIZZA, OTHER
}

enum class KotlinInterestLevel {
    LOW, MEDIUM, HIGH
}

@JvmInline
@Serializable
value class Comment(val value: String)

