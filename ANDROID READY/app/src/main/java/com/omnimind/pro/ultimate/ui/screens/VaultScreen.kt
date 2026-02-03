package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.components.CategoryPill
import com.omnimind.pro.ultimate.ui.theme.*

@Composable
fun VaultScreen(
    notes: List<Note>,
    cats: List<Category>
) {
    var filter by remember { mutableStateOf("All") }
    var search by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(20.dp)) {
        BasicTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom=15.dp)
                .background(OmniGlass, androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                .padding(15.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = OmniText),
            cursorBrush = SolidColor(OmniAccent),
            decorationBox = { inner -> if(search.isEmpty()) Text("Search Vault...", color=OmniTextDim) else inner() }
        )

        Row(modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())) {
            CategoryPill("All", "#38bdf8", filter == "All", { filter = "All" }, textColor = OmniText) // Usage
            cats.forEach { c ->
                CategoryPill(c.n, c.c, filter == c.n, { filter = c.n }, textColor = OmniText) // Usage
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val filtered = notes.filter {
            (filter == "All" || it.cat == filter) &&
            (search.isEmpty() || it.txt.contains(search, ignoreCase = true))
        }.sortedByDescending { it.pinned }

        LazyColumn {
            items(filtered) { n ->
                // NoteCard placeholder logic
                Column(modifier = Modifier
                    .padding(bottom=15.dp)
                    .background(OmniPanel, androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                    .padding(22.dp)
                ) {
                    Text(n.cat.uppercase(), color = try { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(cats.find{it.n==n.cat}?.c ?: "#ffffff")) } catch(e:Exception){OmniText}, fontSize = 10.sp)
                    Text(n.txt, color = OmniText)
                }
            }
        }
    }
}
