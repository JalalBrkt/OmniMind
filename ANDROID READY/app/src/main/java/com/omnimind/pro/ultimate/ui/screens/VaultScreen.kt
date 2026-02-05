package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.components.CategoryPill
import com.omnimind.pro.ultimate.ui.theme.*

@Composable
fun VaultScreen(
    notes: MutableList<Note>,
    cats: List<Category>,
    onUpdate: () -> Unit
) {
    var filter by remember { mutableStateOf("All") }
    var search by remember { mutableStateOf("") }
    var editingNote by remember { mutableStateOf<Note?>(null) }

    // Force recomposition signal
    var updateTrigger by remember { mutableStateOf(0) }

    if (editingNote != null) {
        EditNoteDialog(
            note = editingNote!!,
            cats = cats,
            onDismiss = { editingNote = null },
            onSave = { txt, cat, due, pinned ->
                val index = notes.indexOf(editingNote!!)
                if (index != -1) {
                    notes[index] = editingNote!!.copy(txt=txt, cat=cat, due=due, pinned=pinned)
                    onUpdate()
                    updateTrigger++
                }
                editingNote = null
            }
        )
    }

    Column(modifier = Modifier.padding(20.dp)) {
        BasicTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom=15.dp)
                .background(OmniGlass, RoundedCornerShape(18.dp))
                .padding(15.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = OmniText),
            cursorBrush = SolidColor(OmniAccent),
            decorationBox = { inner -> if(search.isEmpty()) Text("Search Vault...", color=OmniTextDim) else inner() }
        )

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

        Spacer(modifier = Modifier.height(10.dp))

        // Dependent on updateTrigger to force sort re-eval
        val filtered = remember(notes, filter, search, updateTrigger) {
             notes.filter {
                (filter == "All" || it.cat == filter) &&
                (search.isEmpty() || it.txt.contains(search, ignoreCase = true))
            }.sortedByDescending { it.pinned }
        }

        LazyColumn {
            items(filtered, key = { it.id }) { n ->
                NoteCard(n, cats,
                    onEdit = { editingNote = n },
                    onDelete = {
                        notes.remove(n)
                        onUpdate()
                        updateTrigger++
                    },
                    onPin = {
                        val index = notes.indexOf(n)
                        if(index != -1) {
                            notes[index] = n.copy(pinned = !n.pinned)
                            onUpdate()
                            updateTrigger++
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NoteCard(
    n: Note,
    cats: List<Category>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val words = n.txt.split("\\s+".toRegex())
    val isLong = words.size > 50
    val displayTxt = if (isLong && !expanded) words.take(50).joinToString(" ") + "..." else n.txt

    Box(modifier = Modifier
        .padding(bottom=15.dp)
        .background(OmniPanel, RoundedCornerShape(24.dp))
        .padding(22.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(n.cat.uppercase(), color = try { Color(android.graphics.Color.parseColor(cats.find{it.n==n.cat}?.c ?: "#ffffff")) } catch(e:Exception){OmniText}, fontSize = 10.sp)
                if(n.pinned) Text("📌", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(displayTxt, color = OmniText)

            if (isLong) {
                Text(
                    text = if(expanded) "SHOW LESS" else "READ MORE",
                    color = OmniAccent,
                    fontSize = 10.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { expanded = !expanded }
                )
            }
        }

        Box(modifier = Modifier.align(Alignment.TopEnd)) {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.MoreVert, "Options", tint = OmniTextDim)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(OmniPanel).border(1.dp, OmniBorder)
            ) {
                DropdownMenuItem(
                    text = { Text(if(n.pinned) "Unpin" else "Pin", color = OmniText) },
                    onClick = { onPin(); showMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Edit", color = OmniText) },
                    onClick = { onEdit(); showMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = OmniDanger) },
                    onClick = { onDelete(); showMenu = false }
                )
            }
        }
    }
}

@Composable
fun EditNoteDialog(
    note: Note,
    cats: List<Category>,
    onDismiss: () -> Unit,
    onSave: (String, String, String?, Boolean) -> Unit
) {
    var txt by remember { mutableStateOf(note.txt) }
    var cat by remember { mutableStateOf(note.cat) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OmniPanel,
        title = { Text("Edit Memory", color = OmniAccent) },
        text = {
            Column {
                BasicTextField(
                    value = txt,
                    onValueChange = { txt = it },
                    textStyle = androidx.compose.ui.text.TextStyle(color = OmniText),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OmniGlass, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    cats.forEach { c ->
                        CategoryPill(c.n, c.c, cat == c.n, OmniText) { cat = c.n }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(txt, cat, note.due, note.pinned) }, colors = ButtonDefaults.buttonColors(containerColor = OmniAccent)) {
                Text("Save", color = OmniBg)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = OmniTextDim) }
        }
    )
}
