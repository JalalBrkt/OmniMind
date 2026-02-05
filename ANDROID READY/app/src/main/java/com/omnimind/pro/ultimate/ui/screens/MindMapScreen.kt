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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.theme.OmniAccent
import com.omnimind.pro.ultimate.ui.theme.OmniText
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MindMapScreen(cats: List<Category>, notes: List<Note>) {
    Box(modifier = Modifier.fillMaxSize()) {
        if(cats.isEmpty()) {
            Text("No Clusters Found", color = OmniText, modifier = Modifier.align(Alignment.Center))
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                val cy = size.height / 2

                // Draw Vault Center
                drawCircle(OmniAccent, radius = 80f, center = Offset(cx, cy))

                val r = 350f
                cats.forEachIndexed { i, c ->
                    val angle = (i.toFloat() / cats.size) * 2 * Math.PI
                    val x = cx + cos(angle).toFloat() * r
                    val y = cy + sin(angle).toFloat() * r

                    // Draw Connection
                    drawLine(Color.Gray.copy(alpha = 0.3f), start = Offset(cx, cy), end = Offset(x, y), strokeWidth = 2f)

                    // Draw Category Planet
                    val catColor = try { Color(android.graphics.Color.parseColor(c.c)) } catch(e:Exception){ Color.Gray }
                    drawCircle(catColor, radius = 50f, center = Offset(x, y))
                }
            }
            // Overlay text (simplified for prototype as Canvas text requires TextMeasurer)
            Text("VAULT", color = Color.Black, modifier = Modifier.align(Alignment.Center), fontSize = 10.sp)
        }
    }
}
