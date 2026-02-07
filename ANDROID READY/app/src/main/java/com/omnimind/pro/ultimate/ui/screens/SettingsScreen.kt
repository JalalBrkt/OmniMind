package com.omnimind.pro.ultimate.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.ui.theme.*

@Composable
fun SettingsDialog(
    cats: List<Category>,
    onDismiss: () -> Unit,
    onExport: (String) -> Unit, // Takes category name or "All"
    onImport: () -> Unit,
    onWipe: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedExportCat by remember { mutableStateOf("All") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = OmniPanel)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Settings & Backup", color = OmniText, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = OmniTextDim)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("DATA MANAGEMENT", color = OmniTextDim, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                // Export Selection
                Box {
                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = OmniGlass),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Export: $selectedExportCat", color = OmniAccent)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(OmniPanel)
                    ) {
                        DropdownMenuItem(text = { Text("All Clusters", color=OmniText) }, onClick = { selectedExportCat = "All"; expanded = false })
                        cats.forEach { c ->
                            DropdownMenuItem(text = { Text(c.n, color=OmniText) }, onClick = { selectedExportCat = c.n; expanded = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onExport(selectedExportCat) },
                    colors = ButtonDefaults.buttonColors(containerColor = OmniAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Export File", color = OmniBg)
                }

                Spacer(modifier = Modifier.height(15.dp))
                Divider(color = OmniBorder)
                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = onImport,
                    colors = ButtonDefaults.buttonColors(containerColor = OmniGlass),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import Data (JSON)", color = OmniText)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("DANGER ZONE", color = OmniDanger, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onWipe,
                    colors = ButtonDefaults.buttonColors(containerColor = OmniGlass),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Wipe All Data", color = OmniDanger)
                }
            }
        }
    }
}
