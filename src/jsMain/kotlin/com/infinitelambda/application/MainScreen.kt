package com.infinitelambda.application

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.ktor.client.*

private sealed class NavigationTarget(val name: String, val icon: ImageVector) {

    data object Form : NavigationTarget(name = "Feedback Form", icon = Icons.Default.List)

    data object Dashboard : NavigationTarget(name = "Results", icon = Icons.Default.Info)

}

private val navigationTargets = listOf(NavigationTarget.Form, NavigationTarget.Dashboard)

@Composable
fun MainScreen(paddingValues: PaddingValues, client: HttpClient) {
    var selectedTarget: NavigationTarget by remember { mutableStateOf(NavigationTarget.Form) }

    Column {
        TabRow(selectedTabIndex = navigationTargets.indexOf(selectedTarget)) {
            navigationTargets.forEachIndexed { index, target ->
                Tab(
                    selected = target == selectedTarget,
                    text = { Text(target.name) },
                    icon = { Icon(target.icon, contentDescription = "${target.name} tab") },
                    onClick = { selectedTarget = navigationTargets[index] }
                )
            }
        }
        Box(modifier = Modifier.padding(paddingValues)
            .fillMaxSize()) {
            when (selectedTarget) {
                NavigationTarget.Dashboard -> DashboardScreen(client)
                NavigationTarget.Form -> FormScreen(client)
            }
        }
    }

}