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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.omnimind.pro.ultimate.ui.theme.*

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onWipe: () -> Unit
) {
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

                Button(
                    onClick = onExport,
                    colors = ButtonDefaults.buttonColors(containerColor = OmniGlass),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export Data (JSON)", color = OmniAccent)
                }

                Spacer(modifier = Modifier.height(10.dp))

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
