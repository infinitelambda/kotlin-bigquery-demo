package com.infinitelambda.application

import com.infinitelambda.application.environment.Dependencies
import com.infinitelambda.application.environment.Environment
import com.infinitelambda.application.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers


fun main() = cancelOnShutdown(Dispatchers.Default) {
    val environment = Environment()
    val dependencies = Dependencies(environment)
    dependencies.use {
        embeddedServer(Netty, port = environment.http.port, host = environment.http.host) {
            module(it)
        }.start(wait = true)
    }
}

internal fun Application.module(dependencies: Dependencies) {
    configureContentNegotiation()
    configureCors()
    configureCallLogging()
    configureWebSockets()

    with(dependencies) {
        configureRouting()
    }
}