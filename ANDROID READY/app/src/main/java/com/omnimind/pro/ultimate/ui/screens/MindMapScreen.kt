package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.theme.OmniAccent
import com.omnimind.pro.ultimate.ui.theme.OmniBg
import com.omnimind.pro.ultimate.ui.theme.OmniBorder
import com.omnimind.pro.ultimate.ui.theme.OmniText
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun MindMapScreen(cats: List<Category>, notes: List<Note>) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val cx = maxWidth.value / 2
        val cy = maxHeight.value / 2
        val radius = 120f

        // Central Node
        Box(
            modifier = Modifier
                .offset(x = (cx - 40).dp, y = (cy - 40).dp)
                .size(80.dp)
                .background(OmniAccent, CircleShape)
                .border(2.dp, OmniBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("VAULT", color = OmniBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        // Orbiting Nodes
        cats.forEachIndexed { i, c ->
            val angle = (i.toFloat() / cats.size) * 2 * Math.PI
            val x = (cx - 30) + (cos(angle) * radius)
            val y = (cy - 30) + (sin(angle) * radius)

            val catColor = try { Color(android.graphics.Color.parseColor(c.c)) } catch(e:Exception){ OmniAccent }

            Box(
                modifier = Modifier
                    .offset(x = x.dp, y = y.dp)
                    .size(60.dp)
                    .background(OmniBg, CircleShape)
                    .border(2.dp, catColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(c.n, color = OmniText, fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}
