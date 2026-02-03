package com.omnimind.pro.ultimate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.ui.theme.*

@Composable
fun FilterPill(text: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .background(if (active) AccentColor else GlassBorder, CircleShape)
            .border(1.dp, if (active) AccentColor else GlassBorder, CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (active) BgColor else TextDimColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}
