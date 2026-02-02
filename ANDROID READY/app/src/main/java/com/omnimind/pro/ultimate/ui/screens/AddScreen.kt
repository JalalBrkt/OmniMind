package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.DataRepository
import com.omnimind.pro.ultimate.Note
import com.omnimind.pro.ultimate.ui.components.FilterPill
import com.omnimind.pro.ultimate.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(repo: DataRepository) {
    var txt by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("General") }

    val cats = repo.data.cats

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Text("SELECT CLUSTER", color = TextDimColor, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text("MANAGE", color = AccentColor, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.clickable { /* Manage cats */ })
        }

        Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 15.dp)) {
            cats.forEach { cat ->
                FilterPill(cat.n, selectedCat == cat.n) { selectedCat = cat.n }
            }
        }

        OutlinedTextField(
            value = txt,
            onValueChange = { txt = it },
            placeholder = { Text("What did you learn today?", color = TextDimColor) },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 15.dp),
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = GlassBorder,
                focusedBorderColor = AccentColor,
                unfocusedBorderColor = GlassBorder,
                textColor = TextColor,
                cursorColor = AccentColor
            )
        )

        Button(
            onClick = {
                if (txt.isNotBlank()) {
                    val note = Note(
                        txt = txt,
                        cat = selectedCat,
                        due = if (selectedCat == "Tasks") LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME) else null
                    )
                    repo.addNote(note)
                    txt = ""
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
        ) {
            Text("Lock into Vault", color = BgColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}
