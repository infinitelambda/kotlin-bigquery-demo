package com.infinitelambda.application.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriodMillis = 15.seconds.inWholeMilliseconds
        timeoutMillis = 15.seconds.inWholeMilliseconds
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
}