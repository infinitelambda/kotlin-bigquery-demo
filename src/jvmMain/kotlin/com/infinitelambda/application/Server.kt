package com.infinitelambda.application

import com.infinitelambda.application.environment.Dependencies
import com.infinitelambda.application.environment.Environment
import com.infinitelambda.application.route.formResultRoutes
import com.infinitelambda.application.route.index
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.html.*


fun main() = cancelOnShutdown(Dispatchers.Default) {
    val environment = Environment()
    val dependencies = Dependencies(environment)
    dependencies.use {
        embeddedServer(Netty, port = environment.http.port, host = environment.http.host) {
            app(it)
        }.start(wait = true)
    }
}

internal fun Application.app(dependencies: Dependencies) {
    install(ContentNegotiation) {
        json()
    }

    install(CallLogging)

    routing {
        index()

        with(dependencies.writeStream) {
            formResultRoutes()
        }
    }
}