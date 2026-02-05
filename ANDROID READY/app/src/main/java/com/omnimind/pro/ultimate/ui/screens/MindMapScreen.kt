package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MindMapScreen(cats: List<Category>, notes: List<Note>) {
    var rootCat by remember { mutableStateOf<String?>(null) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    if (selectedNote != null) {
        AlertDialog(
            onDismissRequest = { selectedNote = null },
            confirmButton = { TextButton(onClick = { selectedNote = null }) { Text("Close", color = OmniText) } },
            title = { Text(selectedNote!!.cat.uppercase(), color = OmniTextDim, fontSize = 12.sp) },
            text = { Text(selectedNote!!.txt, color = OmniText) },
            containerColor = OmniPanel
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val cx = maxWidth.value / 2
        val cy = maxHeight.value / 2
        val radius = 120f

        // Central Node
        Box(
            modifier = Modifier
                .offset(x = (cx - 40).dp, y = (cy - 40).dp)
                .size(80.dp)
                .background(if (rootCat == null) OmniAccent else try { Color(android.graphics.Color.parseColor(cats.find { it.n == rootCat }?.c)) } catch(e:Exception){OmniAccent}, CircleShape)
                .border(2.dp, OmniBorder, CircleShape)
                .clickable { if (rootCat != null) rootCat = null }, // Reset on click if deep
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rootCat ?: "VAULT",
                color = OmniBg,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1
            )
        }

        if (rootCat == null) {
            // Level 1: Categories
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
                        .border(2.dp, catColor, CircleShape)
                        .clickable { rootCat = c.n },
                    contentAlignment = Alignment.Center
                ) {
                    Text(c.n, color = OmniText, fontSize = 10.sp, maxLines = 1)
                }
            }
        } else {
            // Level 2: Notes in Category
            val catNotes = notes.filter { it.cat == rootCat }.take(8) // Limit to 8

            if (catNotes.isEmpty()) {
                Box(modifier = Modifier.offset(x = (cx - 30).dp, y = (cy + 80).dp).size(60.dp), contentAlignment = Alignment.Center) {
                    Text("Empty", color = OmniTextDim, fontSize = 10.sp)
                }
            }

            catNotes.forEachIndexed { i, n ->
                val angle = (i.toFloat() / catNotes.size) * 2 * Math.PI
                val x = (cx - 30) + (cos(angle) * radius)
                val y = (cy - 30) + (sin(angle) * radius)

                Box(
                    modifier = Modifier
                        .offset(x = x.dp, y = y.dp)
                        .size(60.dp)
                        .background(OmniPanel, CircleShape)
                        .border(1.dp, OmniBorder, CircleShape)
                        .clickable { selectedNote = n },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = n.txt.take(10) + "..",
                        color = OmniText,
                        fontSize = 9.sp,
                        maxLines = 2,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}
