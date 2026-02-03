package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.theme.OmniAccent
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MindMapScreen(cats: List<Category>, notes: List<Note>) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2

            drawCircle(OmniAccent, radius = 100f, center = Offset(cx, cy))

            val r = 300f
            cats.forEachIndexed { i, c ->
                val angle = (i.toFloat() / cats.size) * 2 * Math.PI
                val x = cx + cos(angle).toFloat() * r
                val y = cy + sin(angle).toFloat() * r

                drawCircle(Color.Gray, radius = 60f, center = Offset(x, y))
            }
        }
    }
}
