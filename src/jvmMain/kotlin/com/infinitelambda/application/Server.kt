package com.infinitelambda.application

import com.infinitelambda.application.environment.Dependencies
import com.infinitelambda.application.environment.Environment
import com.infinitelambda.application.route.dashboardRoutes
import com.infinitelambda.application.route.formResultRoutes
import com.infinitelambda.application.route.index
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.server.websocket.WebSockets
import kotlinx.coroutines.Dispatchers
import kotlinx.html.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


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

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowSameOrigin = true
        allowNonSimpleContentTypes = true
        anyHost()
    }

    install(CallLogging) {
        level = Level.INFO
        format {
            val httpMethod = it.request.httpMethod
            val requestUri = it.request.uri
            val status = it.response.status()
            "Request $httpMethod $requestUri handled with status $status"
        }
    }

    install(WebSockets) {
        pingPeriodMillis = 15.seconds.inWholeMilliseconds
        timeoutMillis = 15.seconds.inWholeMilliseconds
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }

    routing {
        staticResources("static", ".")

        index()

        with(dependencies.writeStream, dependencies.languageServiceClient) {
            formResultRoutes()
        }

        with(dependencies.readStream, this@app) {
            this@routing.dashboardRoutes()
        }
    }
}