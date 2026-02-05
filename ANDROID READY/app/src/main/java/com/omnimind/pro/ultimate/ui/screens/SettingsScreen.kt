package com.omnimind.pro.ultimate.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.data.Repository
import com.omnimind.pro.ultimate.ui.theme.OmniText
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun SettingsScreen(
    repo: Repository,
    notes: List<Note>,
    cats: List<Category>,
    onImport: (Pair<List<Note>, List<Category>>) -> Unit
) {
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                // Quick serialize
                val json = com.google.gson.Gson().toJson(Repository.Wrapper(notes, cats))
                stream.write(json.toByteArray())
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                val sb = StringBuilder()
                context.contentResolver.openInputStream(it)?.use { stream ->
                    BufferedReader(InputStreamReader(stream)).forEachLine { line -> sb.append(line) }
                }
                val wrapper = com.google.gson.Gson().fromJson(sb.toString(), Repository.Wrapper::class.java)
                onImport(Pair(wrapper.notes, wrapper.cats))
            } catch(e: Exception) {
                // Handle error
            }
        }
    }

    Column(modifier = Modifier.padding(20.dp)) {
        Text("Data Management", color = OmniText, fontSize = androidx.compose.ui.unit.TextUnit.Unspecified)
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { exportLauncher.launch("omnimind_backup.json") }, modifier = Modifier.fillMaxWidth()) {
            Text("Export Vault")
        }
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { importLauncher.launch(arrayOf("application/json")) }, modifier = Modifier.fillMaxWidth()) {
            Text("Import Vault")
        }
    }
}
