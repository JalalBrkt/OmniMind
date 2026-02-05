package com.omnimind.pro.ultimate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.ui.theme.*

@Composable
fun CategoryPill(
    text: String,
    color: String,
    isActive: Boolean,
    textColor: Color = OmniTextDim, // Moved before lambda
    onClick: () -> Unit // Lambda last for idiomatic usage
) {
    val hex = try { Color(android.graphics.Color.parseColor(color)) } catch(e: Exception) { OmniAccent }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(if (isActive) hex else OmniGlass, RoundedCornerShape(50))
            .border(1.dp, if (isActive) hex else OmniBorder, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) OmniBg else textColor
        )
    }
}
