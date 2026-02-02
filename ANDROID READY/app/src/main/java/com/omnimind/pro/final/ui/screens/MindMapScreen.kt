package com.omnimind.pro.final.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.final.DataRepository
import com.omnimind.pro.final.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MindMapScreen(repo: DataRepository) {
    var root by remember { mutableStateOf<String?>(null) }

    val cats = repo.data.cats
    val notes = repo.data.notes

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(if (root == null) AccentColor else try { Color(android.graphics.Color.parseColor(cats.find{it.n==root}?.c?:"#38bdf8")) } catch(e:Exception){AccentColor})
                .clickable { root = null }
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = root ?: "VAULT",
                color = BgColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        if (root == null) {
            val count = cats.size
            cats.forEachIndexed { i, cat ->
                val angle = (i.toDouble() / count) * 2 * Math.PI

                val offsetX = (cos(angle) * 130).dp
                val offsetY = (sin(angle) * 130).dp

                OrbitNode(
                    text = cat.n,
                    color = try { Color(android.graphics.Color.parseColor(cat.c)) } catch(e: Exception) { Color.White },
                    offsetX = offsetX,
                    offsetY = offsetY,
                    onClick = { root = cat.n }
                )
            }
        } else {
            val catNotes = notes.filter { it.cat == root }.take(8)
            val count = catNotes.size
            if (count == 0) {
                 Text("Empty", color = TextDimColor, modifier = Modifier.offset(y = 100.dp))
            } else {
                catNotes.forEachIndexed { i, note ->
                    val angle = (i.toDouble() / count) * 2 * Math.PI
                    val offsetX = (cos(angle) * 150).dp
                    val offsetY = (sin(angle) * 150).dp

                    OrbitNode(
                        text = note.txt.take(10) + "..",
                        color = PanelColor,
                        borderColor = TextDimColor,
                        offsetX = offsetX,
                        offsetY = offsetY,
                        onClick = { }
                    )
                }
            }
        }

        Text(
            "Tap Center to Reset • Tap Node to Expand",
            color = TextDimColor,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
        )
    }
}

@Composable
fun OrbitNode(
    text: String,
    color: Color,
    borderColor: Color = Color.Transparent,
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(60.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, if (borderColor != Color.Transparent) borderColor else color, CircleShape)
            .clickable { onClick() }
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            color = if (color == PanelColor) TextColor else BgColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 10.sp
        )
    }
}
