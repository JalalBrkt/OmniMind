package com.omnimind.pro.final.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.final.DataRepository
import com.omnimind.pro.final.Note
import com.omnimind.pro.final.ui.components.FilterPill
import com.omnimind.pro.final.ui.components.MarkdownText
import com.omnimind.pro.final.ui.theme.*
import kotlin.math.abs

@Composable
fun ReviewScreen(repo: DataRepository) {
    var catFilter by remember { mutableStateOf("All") }
    val notes = repo.data.notes
    val cats = repo.data.cats

    var stack by remember(catFilter, notes) {
        mutableStateOf(
            (if (catFilter == "All") notes else notes.filter { it.cat == catFilter })
            .shuffled()
        )
    }

    var index by remember(catFilter, notes) { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp)
    ) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 20.dp)) {
            FilterPill("All", catFilter == "All") { catFilter = "All" }
            cats.forEach { cat ->
                FilterPill(cat.n, catFilter == cat.n) { catFilter = cat.n }
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (index < stack.size) {
                SwipeCard(
                    note = stack[index],
                    repo = repo,
                    onSwipe = {
                        index++
                    }
                )
            } else {
                Text("No more memories to review.", color = TextDimColor)
            }
        }
    }
}

@Composable
fun SwipeCard(note: Note, repo: DataRepository, onSwipe: () -> Unit) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var rotation by remember { mutableFloatStateOf(0f) }

    val animatedOffsetX by animateFloatAsState(targetValue = offsetX, label = "offset")
    val animatedRotation by animateFloatAsState(targetValue = rotation, label = "rotation")

    val catColor = repo.data.cats.find { it.n == note.cat }?.c ?: "#ffffff"
    val colorObj = try { Color(android.graphics.Color.parseColor(catColor)) } catch(e: Exception) { Color.White }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .graphicsLayer {
                translationX = animatedOffsetX
                rotationZ = animatedRotation
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > 300) {
                            onSwipe()
                        } else {
                            offsetX = 0f
                            rotation = 0f
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        rotation = offsetX / 10
                    }
                )
            },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(30.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(colorObj)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(note.cat.uppercase(), color = TextDimColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                MarkdownText(text = note.txt, color = TextColor)
            }
        }
    }
}
