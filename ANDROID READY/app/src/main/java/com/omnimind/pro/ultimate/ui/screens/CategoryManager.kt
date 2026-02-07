package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.ui.theme.*

@Composable
fun CategoryManagerDialog(
    cats: MutableList<Category>,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit
) {
    var newCatName by remember { mutableStateOf("") }
    var newCatColor by remember { mutableStateOf("#38bdf8") }
    // Only lock "Tasks" completely. Others are editable.
    val lockedCats = listOf("Tasks")
    val colors = listOf("#38bdf8", "#fbbf24", "#10b981", "#ef4444", "#a855f7", "#ec4899", "#f97316", "#64748b")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OmniPanel,
        title = { Text("Manage Clusters", color = OmniText) },
        text = {
            Column(modifier = Modifier.height(400.dp)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cats) { c ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .background(OmniGlass, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(modifier = Modifier.size(20.dp).background(try{Color(android.graphics.Color.parseColor(c.c))}catch(e:Exception){OmniAccent}, RoundedCornerShape(50)))
                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Editable Name if not locked
                                    if (lockedCats.contains(c.n)) {
                                        Text(c.n, color = OmniText, fontSize = 14.sp)
                                    } else {
                                        var editName by remember { mutableStateOf(c.n) }
                                        BasicTextField(
                                            value = editName,
                                            onValueChange = {
                                                editName = it
                                                c.n = it // Direct mutation for simplicity, followed by update
                                                onUpdate()
                                            },
                                            textStyle = TextStyle(color = OmniText, fontSize = 14.sp),
                                            cursorBrush = SolidColor(OmniAccent),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                                if (!lockedCats.contains(c.n)) {
                                    IconButton(onClick = {
                                        cats.remove(c)
                                        onUpdate()
                                    }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Close, "Delete", tint = OmniDanger)
                                    }
                                }
                            }

                            // Color Picker Row (if not locked)
                            if (!lockedCats.contains(c.n)) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    colors.forEach { col ->
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(try{Color(android.graphics.Color.parseColor(col))}catch(e:Exception){Color.Gray}, RoundedCornerShape(50))
                                                .border(if(c.c == col) 2.dp else 0.dp, OmniText, RoundedCornerShape(50))
                                                .clickable {
                                                    val idx = cats.indexOf(c)
                                                    if (idx != -1) {
                                                        cats[idx] = c.copy(c = col)
                                                        onUpdate()
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))
                Divider(color = OmniBorder)
                Spacer(modifier = Modifier.height(15.dp))

                Text("Add New Cluster", color = OmniTextDim, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = newCatName,
                        onValueChange = { newCatName = it },
                        modifier = Modifier
                            .weight(1f)
                            .background(OmniGlass, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = OmniText),
                        cursorBrush = SolidColor(OmniAccent),
                         decorationBox = { inner -> if(newCatName.isEmpty()) Text("Name...", color=OmniTextDim) else inner() }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (newCatName.isNotEmpty() && cats.none { it.n == newCatName }) {
                                cats.add(Category(newCatName, newCatColor))
                                newCatName = ""
                                onUpdate()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OmniAccent)
                    ) {
                        Text("Add", color = OmniBg)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done", color = OmniText) }
        }
    )
}
