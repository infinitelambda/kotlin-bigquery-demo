package com.infinitelambda.application.route

import com.infinitelambda.application.bigQuery.BigQueryReadStream
import com.infinitelambda.application.service.DashboardService
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

context(BigQueryReadStream)
fun Routing.dashboardRoutes() {
    webSocket("/dashboard") {
        DashboardService.observeDashboardData().collect {
            sendSerialized(it)
        }
    }
}