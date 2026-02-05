package com.omnimind.pro.ultimate.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.components.CategoryPill
import com.omnimind.pro.ultimate.ui.theme.*
import java.util.Calendar

@Composable
fun AddScreen(
    cats: List<Category>,
    onSave: (Note) -> Unit
) {
    var txt by remember { mutableStateOf("") }
    var selCat by remember { mutableStateOf("General") }
    var due by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(20.dp)) {
        // Categories
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            cats.forEach { c ->
                CategoryPill(
                    text = c.n,
                    color = c.c,
                    isActive = selCat == c.n,
                    textColor = OmniText,
                    onClick = { selCat = c.n }
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        if (selCat == "Tasks") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OmniGlass, androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                    .clickable {
                        val c = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            TimePickerDialog(context, { _, h, min ->
                                due = String.format("%04d-%02d-%02dT%02d:%02d", y, m + 1, d, h, min)
                            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
                    }
                    .padding(18.dp)
            ) {
                Text(
                    text = if (due.isEmpty()) "Set Due Date..." else "Due: $due",
                    color = if (due.isEmpty()) OmniTextDim else OmniAccent
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }

        // Note Input
        BasicTextField(
            value = txt,
            onValueChange = { txt = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(OmniGlass, androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                .padding(18.dp),
            textStyle = TextStyle(color = OmniText),
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
