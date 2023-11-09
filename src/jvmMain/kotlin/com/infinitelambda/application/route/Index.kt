package com.infinitelambda.application.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Routing.index() {
    get("/") {
        call.respondHtml(HttpStatusCode.OK, HTML::index)
    }
}

private fun HTML.index() {
    head {
        title("Real-time data analytics with Kotlin and BigQuery")
    }
    body {
        div {
            +"Hello from Ktor"
        }
        div {
            id = "root"
        }
        script(src = "/static/kotlin-bigquery-demo.js") {}
    }
}