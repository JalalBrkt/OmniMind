package com.omnimind.pro.final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.omnimind.pro.final.ui.MainApp
import com.omnimind.pro.final.ui.theme.OmniMindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = DataRepository(this)

        setContent {
            OmniMindTheme {
                MainApp(repo)
            }
        }
    }
}
