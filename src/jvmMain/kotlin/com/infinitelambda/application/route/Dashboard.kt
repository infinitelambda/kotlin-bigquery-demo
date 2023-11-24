package com.infinitelambda.application.route

import com.infinitelambda.application.bigQuery.BigQueryReadStream
import com.infinitelambda.application.service.DashboardService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn

context(BigQueryReadStream, CoroutineScope)
fun Routing.dashboardRoutes() {

    val broadcastChannel = DashboardService.observeDashboardData()
        .shareIn(this@CoroutineScope, started = SharingStarted.Lazily, replay = 1)

    webSocket("/dashboard") {
        call.application.log.info("New web socket session initiated")

        broadcastChannel.collect {
            sendSerialized(it)
        }
    }
}