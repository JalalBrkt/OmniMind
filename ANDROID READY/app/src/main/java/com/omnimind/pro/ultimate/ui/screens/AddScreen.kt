package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.components.CategoryPill
import com.omnimind.pro.ultimate.ui.theme.*

@Composable
fun AddScreen(
    cats: MutableList<Category>,
    onSave: (Note) -> Unit
) {
    var txt by remember { mutableStateOf("") }
    var selCat by remember { mutableStateOf("General") }
    var due by remember { mutableStateOf("") }
    var showCatMgr by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showCatMgr) {
        CategoryManagerDialog(
            cats = cats,
            onDismiss = { showCatMgr = false },
            onUpdate = { /* Triggered */ }
        )
    }

    Column(modifier = Modifier.padding(20.dp)) {
        // Categories Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SELECT CLUSTER", color = OmniTextDim, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text("MANAGE", color = OmniAccent, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.clickable { showCatMgr = true })
        }

        // Categories List
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            cats.forEach { c ->
                CategoryPill(
                    text = c.n,
                    color = c.c,
                    isActive = selCat == c.n,
                    textColor = OmniText, // Explicit parameter
                    onClick = { selCat = c.n } // Trailing lambda logic
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        if (selCat == "Tasks") {
            BasicTextField(
                value = due,
                onValueChange = { due = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OmniGlass, androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                    .padding(18.dp),
                textStyle = androidx.compose.ui.text.TextStyle(color = OmniText),
                cursorBrush = SolidColor(OmniAccent),
                decorationBox = { inner -> if(due.isEmpty()) Text("Due Date (YYYY-MM-DD HH:MM)...", color=OmniTextDim) else inner() }
            )
            Spacer(modifier = Modifier.height(15.dp))
        }

        BasicTextField(
            value = txt,
            onValueChange = { txt = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(OmniGlass, androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                .padding(18.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = OmniText),
            cursorBrush = SolidColor(OmniAccent),
            decorationBox = { inner -> if(txt.isEmpty()) Text("What did you learn today?", color=OmniTextDim) else inner() }
        )

        Button(
            onClick = {
                if(txt.isNotEmpty()) {
                    onSave(Note(txt = txt, cat = selCat, due = if(selCat=="Tasks") due else null))
                    txt = ""
                    due = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OmniAccent),
            modifier = Modifier.fillMaxWidth().padding(top = 15.dp)
        ) {
            Text("Lock into Vault", color = OmniBg)
        }
    }
}
