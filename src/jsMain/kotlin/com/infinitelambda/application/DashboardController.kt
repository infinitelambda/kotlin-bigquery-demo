package com.infinitelambda.application

import com.infinitelambda.application.data.DashboardData
import com.infinitelambda.application.html.favouriteFoodChartId
import com.infinitelambda.application.html.kotlinInterestLevelChartId
import com.infinitelambda.application.html.sentimentChartId
import google.visualization.DataTable
import google.visualization.PieChart
import google.visualization.PieChartOptions
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.*
import js.process
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DashboardController(client: HttpClient, scope: CoroutineScope) {

    init {
        val favouriteFoodChart = PieChart(document.getElementById(favouriteFoodChartId))
        val kotlinInterestLevelChart = PieChart(document.getElementById(kotlinInterestLevelChartId))
        val sentimentChart = PieChart(document.getElementById(sentimentChartId))

        scope.launch {
            observeData(client) {
                favouriteFoodChart.draw(
                    data = DataTable().apply {
                        addColumn("string", "Food")
                        addColumn("number", "Popularity")
                        addRows(
                            arrayOf(
                                arrayOf("Pizza", it.favouriteFood.pizza.value),
                                arrayOf("Burger", it.favouriteFood.burger.value),
                            )
                        )
                    },
                    options = PieChartOptions().apply {
                        title = "Favourite Food"
                    }
                )

                kotlinInterestLevelChart.draw(
                    data = DataTable().apply {
                        addColumn("string", "Kotlin Interest Level")
                        addColumn("number", "Popularity")
                        addRows(
                            arrayOf(
                                arrayOf("High", it.kotlinInterestLevel.high.value),
                                arrayOf("Medium", it.kotlinInterestLevel.medium.value),
                                arrayOf("Low", it.kotlinInterestLevel.low.value)
                            )
                        )
                    },
                    options = PieChartOptions().apply {
                        title = "Kotlin Interest Level"
                    }
                )

                sentimentChart.draw(
                    data = DataTable().apply {
                        addColumn("string", "Sentiment")
                        addColumn("number", "Popularity")
                        addRows(
                            arrayOf(
                                arrayOf("Positive", it.sentiment.positive.value),
                                arrayOf("Negative", it.sentiment.negative.value)
                            )
                        )
                    },
                    options = PieChartOptions().apply {
                        title = "Talk Feedback Sentiment"
                    }
                )
            }
        }
    }

    private suspend fun observeData(client: HttpClient, consumer: (DashboardData) -> Unit) {
        client.webSocket(
            request = {
                method = HttpMethod.Get
                url {
                    protocol =  URLProtocol.byName[process.env.WEB_SOCKET_PROTOCOL] ?: URLProtocol.WS
                    path("/dashboard")
                }
            }
        ) {
            incoming.consumeAsFlow()
                .map { converter?.deserialize<DashboardData>(it) }
                .filterNotNull()
                .collect(consumer)
        }
    }
}