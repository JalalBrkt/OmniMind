package com.omnimind.pro.final.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentColor,
    background = BgColor,
    surface = PanelColor,
    onPrimary = BgColor,
    onBackground = TextColor,
    onSurface = TextColor
)

@Composable
fun OmniMindTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
