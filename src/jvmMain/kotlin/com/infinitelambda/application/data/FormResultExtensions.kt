package com.infinitelambda.application.data

import org.json.JSONObject

fun FormResult.asJSONObject(): JSONObject =
    JSONObject().apply {
        put("favourite_food", favouriteFood.name)
        put("kotlin_interest_level", kotlinInterestLevel.name)
        put("comment", comment.value)
    }