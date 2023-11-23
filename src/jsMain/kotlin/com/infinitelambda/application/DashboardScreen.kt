@file:Suppress("FunctionName")

package com.infinitelambda.application

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.infinitelambda.application.data.*
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.ColumnLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.toString
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.flow.flow

private fun Percentage.toFloat() = value.toFloat()

private fun Float.format() = "${(this * 100.0f).toString(2)}%"

private fun observeData(client: HttpClient) = flow<DashboardData> {
    client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/dashboard") {
        while (true) {
            emit(receiveDeserialized())
        }
    }
}

@Composable
fun DashboardScreen(client: HttpClient) {
    val data by remember { observeData(client) }.collectAsState(
        DashboardData(
            favouriteFood = FavouriteFoodData(
                pizza = Percentage(0.0),
                burger = Percentage(0.0)
            ),
            kotlinInterestLevel = KotlinInterestLevelData(
                high = Percentage(0.0),
                medium = Percentage(0.0),
                low = Percentage(0.0)
            ),
            sentiment = SentimentData(
                positive = Percentage(0.0),
                negative = Percentage(0.0)
            )
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, top = 32.dp, end = 16.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DashboardPieChart(
                modifier = Modifier.size(500.dp),
                title = "Favourite Food",
                items = listOf(
                    "Pizza" to data.favouriteFood.pizza.toFloat(),
                    "Burger" to data.favouriteFood.burger.toFloat()
                )
            ) { pair, color ->
                PieChartEntry(pair.second, color, pair.second.format(), pair.first)
            }

            DashboardPieChart(
                modifier = Modifier.size(500.dp),
                title = "Kotlin Interest Level",
                items = listOf(
                    "High" to data.kotlinInterestLevel.high.toFloat(),
                    "Medium" to data.kotlinInterestLevel.medium.toFloat(),
                    "Low" to data.kotlinInterestLevel.low.toFloat()
                )
            ) { pair, color ->
                PieChartEntry(pair.second, color, pair.second.format(), pair.first)
            }
        }

        DashboardPieChart(
            modifier = Modifier.size(650.dp),
            title = "Talk Feedback Sentiment",
            items = listOf(
                "Positive" to data.sentiment.positive.toFloat(),
                "Negative" to data.sentiment.negative.toFloat()
            )
        ) { pair, color ->
            PieChartEntry(pair.second, color, pair.second.format(), pair.first)
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun <T> DashboardPieChart(
    modifier: Modifier = Modifier,
    title: String,
    items: List<T>,
    entryMapper: (T, Color) -> PieChartEntry
) {
    val entries = items.zip(generateHueColorPalette(items.size), entryMapper)

    ChartLayout(
        modifier = modifier.padding(16.dp),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h4
            )
        },
        legend = {
            ColumnLegend(
                itemCount = entries.size,
                symbol = {
                    Symbol(
                        modifier = Modifier.size(16.dp),
                        fillBrush = SolidColor(entries[it].color)
                    )
                },
                label = {
                    Text(
                        text = entries[it].name,
                        style = MaterialTheme.typography.body2
                    )
                },
                value = {
                    Text(
                        text = entries[it].formattedValue,
                        style = MaterialTheme.typography.body2
                    )
                }
            )
        },
        legendLocation = LegendLocation.RIGHT
    ) {
        PieChart(
            values = entries.map { it.value },
            slice = {
                DefaultSlice(
                    color = entries[it].color
                )
            },
            label = {
                Text(
                    text = entries[it].formattedValue,
                    style = MaterialTheme.typography.body2
                )
            },
            forceCenteredPie = true
        )
    }
}

@Immutable
private data class PieChartEntry(
    val value: Float,
    val color: Color,
    val formattedValue: String,
    val name: String
)