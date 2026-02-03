package com.omnimind.pro.ultimate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.omnimind.pro.ultimate.data.Repository
import com.omnimind.pro.ultimate.ui.screens.*
import com.omnimind.pro.ultimate.ui.theme.OmniBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = Repository(this)
        val (notes, cats) = repo.load()

        setContent {
            var screen by remember { mutableStateOf("Vault") }
            Scaffold(
                containerColor = OmniBg,
                bottomBar = { /* Simple Nav Logic */ }
            ) { pad ->
                Box(modifier = Modifier.padding(pad)) {
                    when(screen) {
                        "Vault" -> VaultScreen(notes, cats)
                        "Add" -> AddScreen(cats) { n -> notes.add(n); repo.save(notes, cats); screen="Vault" }
                        "Map" -> MindMapScreen(cats, notes)
                    }
                }
            }
        }
    }
}
