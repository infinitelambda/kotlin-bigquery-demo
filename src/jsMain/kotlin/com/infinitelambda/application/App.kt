@file:Suppress("FunctionName")

package com.infinitelambda.application

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import com.infinitelambda.app.core.ui.theme.AppTheme
import io.ktor.client.*

@Composable
fun App(client: HttpClient) {
    AppTheme {
        Scaffold(topBar = { TopBar() }) {
            MainScreen(it, client)
        }
    }
}

@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = "Kotlin and BigQuery Demo") }
    )
}