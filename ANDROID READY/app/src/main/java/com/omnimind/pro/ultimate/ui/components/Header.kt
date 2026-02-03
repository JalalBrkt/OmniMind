package com.omnimind.pro.ultimate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.ui.theme.TextColor

@Composable
fun Header(onSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("OMNIMIND", fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp, color = TextColor)
        Text("⚙️", fontSize = 20.sp, modifier = Modifier.clickable { onSettings() })
    }
}
