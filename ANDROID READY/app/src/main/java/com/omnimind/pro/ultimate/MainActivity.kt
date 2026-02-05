package com.omnimind.pro.ultimate

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.omnimind.pro.ultimate.data.Repository
import com.omnimind.pro.ultimate.ui.screens.*
import com.omnimind.pro.ultimate.ui.theme.OmniAccent
import com.omnimind.pro.ultimate.ui.theme.OmniBg
import com.omnimind.pro.ultimate.ui.theme.OmniPanel
import com.omnimind.pro.ultimate.ui.theme.OmniText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = Repository(this)
        val (notes, cats) = repo.load()

        setContent {
            var screen by remember { mutableStateOf("Vault") }
            var showStats by remember { mutableStateOf(false) }
            val context = LocalContext.current

            Scaffold(
                containerColor = OmniBg,
                bottomBar = {
                    NavigationBar(containerColor = OmniPanel) {
                        val items = listOf(
                            "Vault" to Icons.Default.Home,
                            "Add" to Icons.Default.Add,
                            "Review" to Icons.Default.Refresh,
                            "Map" to Icons.Default.Info
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
                            Toast.makeText(context, "Locked into Vault", Toast.LENGTH_SHORT).show()
                            // screen="Vault" removed to keep user on Add screen
                        }
                        "Review" -> ReviewScreen(notes, cats)
                        "Map" -> MindMapScreen(cats, notes)
                    }
                }
            }
        }
    }
}
