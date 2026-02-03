package com.omnimind.pro.ultimate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.omnimind.pro.ultimate.ui.MainApp
import com.omnimind.pro.ultimate.ui.theme.OmniMindTheme

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
