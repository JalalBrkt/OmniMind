package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.components.CategoryPill
import com.omnimind.pro.ultimate.ui.theme.*
import kotlin.math.abs

@Composable
fun ReviewScreen(
    notes: List<Note>,
    cats: List<Category>
) {
    var filter by remember { mutableStateOf("All") }
    var pool by remember { mutableStateOf(listOf<Note>()) }
    var index by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }

    // For truncation logic inside the card
    var cardExpanded by remember { mutableStateOf(false) }

    // Reshuffle on entry or filter change
    LaunchedEffect(filter, Unit) {
        val filtered = if(filter == "All") notes else notes.filter { it.cat == filter }
        pool = filtered.shuffled()
        index = 0
        offsetX = 0f
        cardExpanded = false
    }

    fun nextCard() {
         if (pool.isNotEmpty()) {
             if (index >= pool.size - 1) {
                 pool = pool.shuffled()
                 index = 0
             } else {
                 index++
             }
             offsetX = 0f
             cardExpanded = false
         }
    }

    fun prevCard() {
        if (pool.isNotEmpty()) {
            index = if(index - 1 < 0) pool.size - 1 else index - 1
            offsetX = 0f
            cardExpanded = false
        }
    }

    Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            CategoryPill(
                text = "All",
                color = "#38bdf8",
                isActive = filter == "All",
                textColor = OmniText,
                onClick = { filter = "All" }
            )
            cats.forEach { c ->
                CategoryPill(
                    text = c.n,
                    color = c.c,
                    isActive = filter == c.n,
                    textColor = OmniText,
                    onClick = { filter = c.n }
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        ) {
            if (pool.isEmpty()) {
                Text("No memories to review.", color = OmniTextDim)
            } else {
                val n = pool.getOrNull(index)
                if (n != null) {
                    val catColor = try { Color(android.graphics.Color.parseColor(cats.find { it.n == n.cat }?.c ?: "#38bdf8")) } catch(e:Exception){ OmniAccent }

                    val words = n.txt.split("\\s+".toRegex())
                    val isLong = words.size > 50
                    val displayTxt = if (isLong && !cardExpanded) words.take(50).joinToString(" ") + "..." else n.txt

                    // Card (drawn first)
                    Box(
                        modifier = Modifier
                            .offset(x = (offsetX / 2).dp)
                            .graphicsLayer(rotationZ = offsetX / 20)
                            .fillMaxWidth()
                            .height(400.dp)
                            .background(OmniPanel, RoundedCornerShape(30.dp))
                            .border(1.dp, OmniBorder, RoundedCornerShape(30.dp))
                            .padding(30.dp)
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        if (abs(offsetX) > 150) {
                                            if (offsetX > 0) prevCard() else nextCard()
                                        } else {
                                            offsetX = 0f
                                        }
                                    }
                                ) { _, dragAmount ->
                                    offsetX += dragAmount
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(catColor, RoundedCornerShape(50)))
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(n.cat.uppercase(), color = OmniTextDim, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(displayTxt, color = OmniText, fontSize = 18.sp, lineHeight = 28.sp)

                            if (isLong) {
                                Text(
                                    text = if(cardExpanded) "SHOW LESS" else "READ MORE",
                                    color = OmniAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(top = 15.dp, bottom = 15.dp)
                                        .clickable { cardExpanded = !cardExpanded }
                                )
                            }
                        }

                        Text(
                            "${index + 1} / ${pool.size}",
                            color = OmniTextDim,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    // Arrows (drawn last = on top)
                    Box(modifier = Modifier.align(Alignment.CenterStart)) {
                         IconButton(onClick = { prevCard() }) {
                            Icon(Icons.Default.KeyboardArrowLeft, "Previous", tint = OmniTextDim, modifier = Modifier.size(48.dp))
                        }
                    }

                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        IconButton(onClick = { nextCard() }) {
                             Icon(Icons.Default.KeyboardArrowRight, "Next", tint = OmniTextDim, modifier = Modifier.size(48.dp))
                        }
                    }
                } else {
                    LaunchedEffect(pool) { index = 0 }
                }
            }
        }
    }
}
