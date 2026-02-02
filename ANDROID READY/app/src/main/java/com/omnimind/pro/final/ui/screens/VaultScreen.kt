package com.omnimind.pro.final.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.final.DataRepository
import com.omnimind.pro.final.Note
import com.omnimind.pro.final.ui.components.NoteCard
import com.omnimind.pro.final.ui.components.FilterPill
import com.omnimind.pro.final.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(repo: DataRepository) {
    var searchQuery by remember { mutableStateOf("") }
    var catFilter by remember { mutableStateOf("All") }
    var editNote by remember { mutableStateOf<Note?>(null) }

    val notes = repo.data.notes
    val cats = repo.data.cats

    val filtered = notes.filter {
        (catFilter == "All" || it.cat == catFilter) &&
        (searchQuery.isEmpty() || it.txt.contains(searchQuery, ignoreCase = true))
    }.sortedWith(compareByDescending<Note> { it.pinned }.thenByDescending { it.id })

    if (editNote != null) {
        EditNoteDialog(
            note = editNote!!,
            cats = cats.map { it.n },
            onDismiss = { editNote = null },
            onSave = { updated ->
                repo.updateNote(updated)
                editNote = null
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(top = 20.dp, start=16.dp, end=16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Vault...", color = TextDimColor) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = GlassBorder,
                focusedBorderColor = AccentColor,
                unfocusedBorderColor = GlassBorder,
                cursorColor = AccentColor,
                textColor = TextColor
            )
        )

        Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 16.dp)) {
            FilterPill("All", catFilter == "All") { catFilter = "All" }
            cats.forEach { cat ->
                FilterPill(cat.n, catFilter == cat.n) { catFilter = cat.n }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(filtered) { note ->
                NoteCard(note, repo, onEdit = { editNote = note })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteDialog(note: Note, cats: List<String>, onDismiss: () -> Unit, onSave: (Note) -> Unit) {
    var txt by remember { mutableStateOf(note.txt) }
    var cat by remember { mutableStateOf(note.cat) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelColor,
        title = { Text("Edit Memory", color = TextColor) },
        text = {
            Column {
                OutlinedTextField(
                    value = txt,
                    onValueChange = { txt = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = TextColor,
                        containerColor = GlassBorder,
                        cursorColor = AccentColor
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                cats.forEach { c ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { cat = c }.padding(4.dp)
                    ) {
                        RadioButton(
                            selected = (cat == c),
                            onClick = { cat = c },
                            colors = RadioButtonDefaults.colors(selectedColor = AccentColor, unselectedColor = TextDimColor)
                        )
                        Text(c, color = TextColor)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(note.copy(txt = txt, cat = cat)) }, colors = ButtonDefaults.buttonColors(containerColor = AccentColor)) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextDimColor) }
        }
    )
}
