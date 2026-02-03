package com.omnimind.pro.ultimate.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.omnimind.pro.ultimate.DataRepository
import com.omnimind.pro.ultimate.ui.screens.*
import com.omnimind.pro.ultimate.ui.components.Header
import com.omnimind.pro.ultimate.ui.components.SettingsDialog

@Composable
fun MainApp(repo: DataRepository) {
    val navController = rememberNavController()
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsDialog(repo = repo, onDismiss = { showSettings = false })
    }

    Scaffold(
        topBar = { Header(onSettings = { showSettings = true }) },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "vault",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("vault") { VaultScreen(repo) }
            composable("add") { AddScreen(repo) }
            composable("mind") { MindMapScreen(repo) }
            composable("review") { ReviewScreen(repo) }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        "vault" to "Vault",
        "add" to "+ Add",
        "mind" to "Map",
        "review" to "Review"
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { (route, label) ->
            NavigationBarItem(
                icon = { Text(label) },
                label = null,
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
