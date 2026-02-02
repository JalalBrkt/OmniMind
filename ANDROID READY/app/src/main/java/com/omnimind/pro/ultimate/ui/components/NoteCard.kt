package com.omnimind.pro.ultimate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.Note
import com.omnimind.pro.ultimate.DataRepository
import com.omnimind.pro.ultimate.ui.theme.*

@Composable
fun NoteCard(note: Note, repo: DataRepository, onEdit: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val isLong = note.txt.length > 100
    val displayTxt = if (isLong && !expanded) note.txt.take(100) + "..." else note.txt

    val catColor = repo.data.cats.find { it.n == note.cat }?.c ?: "#ffffff"
    val colorObj = try { Color(android.graphics.Color.parseColor(catColor)) } catch(e: Exception) { Color.White }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.cat.uppercase(),
                    color = colorObj,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.pinned) {
                        Text("📌", fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
                    }
                    Box {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = TextDimColor,
                            modifier = Modifier.clickable { menuExpanded = true }
                        )
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(PanelColor)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Unpin/Pin", color = TextColor) },
                                onClick = {
                                    repo.updateNote(note.copy(pinned = !note.pinned))
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit", color = TextColor) },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = DangerColor) },
                                onClick = {
                                    repo.deleteNote(note.id)
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            MarkdownText(text = displayTxt, color = TextColor)

            if (isLong) {
                Text(
                    text = if (expanded) "SHOW LESS" else "READ MORE",
                    color = AccentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp).clickable { expanded = !expanded }
                )
            }
        }
    }
}
