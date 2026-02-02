package com.omnimind.pro.final.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.omnimind.pro.final.DataRepository
import com.omnimind.pro.final.ui.theme.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@Composable
fun SettingsDialog(repo: DataRepository, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val notes = repo.data.notes
    val tasks = notes.filter { it.cat == "Tasks" && !it.notified }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val json = reader.readText()
                repo.importData(json)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PanelColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Settings & Insights", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextColor)
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().background(GlassBorder, RoundedCornerShape(12.dp)).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(notes.size.toString(), "MEMORIES", AccentColor)
                    StatItem(tasks.size.toString(), "TASKS", DangerColor)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Data Management", color = TextDimColor, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        try {
                            val file = File(context.cacheDir, "omnimind_backup.json")
                            file.writeText(com.google.gson.Gson().toJson(repo.data))
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/json"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Export Data"))
                        } catch(e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                ) {
                    Text("Export Data")
                }

                OutlinedButton(
                    onClick = { importLauncher.launch("application/json") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDimColor)
                ) {
                    Text("Import JSON")
                }

                TextButton(
                    onClick = { repo.wipe(); onDismiss() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Wipe All Data", color = DangerColor)
                }

                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close", color = TextDimColor)
                }
            }
        }
    }
}

@Composable
fun StatItem(count: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = TextDimColor)
    }
}
