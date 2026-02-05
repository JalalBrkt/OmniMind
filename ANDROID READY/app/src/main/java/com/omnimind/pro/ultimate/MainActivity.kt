package com.omnimind.pro.ultimate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.result.contract.ActivityResultContracts
import com.omnimind.pro.ultimate.data.Repository
import com.omnimind.pro.ultimate.ui.screens.*
import com.omnimind.pro.ultimate.ui.theme.OmniAccent
import com.omnimind.pro.ultimate.ui.theme.OmniBg
import com.omnimind.pro.ultimate.ui.theme.OmniPanel
import com.omnimind.pro.ultimate.ui.theme.OmniText

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission granted or denied logic
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        val repo = Repository(this)
        val loaded = repo.load()

        // Use mutableStateListOf to prevent crashes during list modification
        val notes = mutableStateListOf(*loaded.first.toTypedArray())
        val cats = mutableStateListOf(*loaded.second.toTypedArray())

        setContent {
            var screen by remember { mutableStateOf("Vault") }

            Scaffold(
                containerColor = OmniBg,
                bottomBar = {
                    NavigationBar(containerColor = OmniPanel) {
                        val items = listOf(
                            "Vault" to Icons.Default.Home,
                            "Add" to Icons.Default.Add,
                            "Map" to Icons.Default.Info,
                            "Settings" to Icons.Default.Settings
                        )
                        items.forEach { (label, icon) ->
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = label, tint = if(screen==label) OmniAccent else OmniText) },
                                label = { Text(label, color = if(screen==label) OmniAccent else OmniText) },
                                selected = screen == label,
                                onClick = { screen = label },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = OmniPanel)
                            )
                        }
                    }
                }
            ) { pad ->
                Box(modifier = Modifier.padding(pad)) {
                    when(screen) {
                        "Vault" -> VaultScreen(notes, cats) { repo.save(notes, cats) }
                        "Add" -> AddScreen(cats) { n ->
                            notes.add(0, n)
                            repo.save(notes, cats)
                            screen="Vault"
                        }
                        "Map" -> MindMapScreen(cats, notes)
                        "Settings" -> SettingsScreen(repo, notes, cats) {
                            // On Import Refresh
                            notes.clear()
                            notes.addAll(it.first)
                            cats.clear()
                            cats.addAll(it.second)
                        }
                    }
                }
            }
        }
    }
}
