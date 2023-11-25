package com.infinitelambda.application.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.configureCallLogging() {
    install(CallLogging) {
        level = Level.INFO
        format {
            val httpMethod = it.request.httpMethod
            val requestUri = it.request.uri
            val status = it.response.status()
            "Request $httpMethod $requestUri handled with status $status"
        }
    }
}