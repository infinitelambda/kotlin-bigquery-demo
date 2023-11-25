package com.infinitelambda.application.plugins

import com.infinitelambda.application.environment.Dependencies
import com.infinitelambda.application.route.dashboardRoutes
import com.infinitelambda.application.route.formResultRoutes
import com.infinitelambda.application.route.index
import com.infinitelambda.application.with
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

context(Dependencies)
fun Application.configureRouting() {
    routing {
        staticResources("static", ".")

        index()

        with(writeStream, sentimentAnalyser) {
            formResultRoutes()
        }

        with(readStream, this@configureRouting) {
            this@routing.dashboardRoutes()
        }
    }
}