package com.omnimind.pro.ultimate

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.data.Repository
import com.omnimind.pro.ultimate.ui.screens.*
import com.omnimind.pro.ultimate.ui.theme.*
import kotlinx.coroutines.delay
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        }
    }

    // Activity Result Launchers for File Operations
    private lateinit var repo: Repository
    private lateinit var notesRef: MutableList<Note>
    private lateinit var catsRef: MutableList<Category>
    private var exportFilter: String? = null // Store filter state

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        if (uri != null) {
            repo.exportToUri(uri, notesRef, catsRef, exportFilter)
            val msg = if(exportFilter == "All" || exportFilter == null) "Exported All" else "Exported $exportFilter"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            val wrapper = repo.importFromUri(uri, notesRef, catsRef) // Pass current lists for merging
            if (wrapper != null) {
                if (wrapper.type == "merged") {
                    // Update in-place logic if mutable, or replace
                    notesRef.clear(); notesRef.addAll(wrapper.notes)
                    catsRef.clear(); catsRef.addAll(wrapper.cats)
                    Toast.makeText(this, "Import Merged Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    notesRef.clear(); notesRef.addAll(wrapper.notes)
                    catsRef.clear(); catsRef.addAll(wrapper.cats)
                    Toast.makeText(this, "Import Replaced DB", Toast.LENGTH_SHORT).show()
                }
                repo.save(notesRef, catsRef)
            } else {
                Toast.makeText(this, "Import Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crash Handler
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            try {
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                val log = File(filesDir, "crash_log.txt")
                log.writeText(sw.toString())
                Log.e("OmniCrash", "Crash recorded", e)
            } catch (ioe: Exception) {
                ioe.printStackTrace()
            }
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        // Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Notifications"
            val descriptionText = "Notifications for due tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("tasks_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Request Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        repo = Repository(this)
        val (loadedNotes, loadedCats) = repo.load()

        val notes = mutableStateListOf<Note>().apply { addAll(loadedNotes) }
        val cats = mutableStateListOf<Category>().apply { addAll(loadedCats) }

        notesRef = notes
        catsRef = cats

        setContent {
            var screen by remember { mutableStateOf("Vault") }
            var showSettings by remember { mutableStateOf(false) }
            val context = LocalContext.current

            // Notification Loop
            LaunchedEffect(Unit) {
                while(true) {
                    delay(30000) // Check every 30s
                    val now = System.currentTimeMillis()
                    notes.forEach { n ->
                        if (n.cat == "Tasks" && n.due != null && !n.notified) {
                            try {
                                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                                val date = sdf.parse(n.due!!)
                                if (date != null && now >= date.time) {
                                    sendNotification(context, n.txt)
                                    n.notified = true
                                    repo.save(notes, cats)
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                    }
                }
            }

            if (showSettings) {
                SettingsDialog(
                    cats = cats,
                    onDismiss = { showSettings = false },
                    onExport = { cat ->
                        exportFilter = cat
                        val name = if(cat == "All") "omnimind_backup.json" else "omnimind_${cat}.json"
                        exportLauncher.launch(name)
                    },
                    onImport = { importLauncher.launch(arrayOf("application/json")) },
                    onWipe = {
                        notes.clear()
                        cats.clear(); cats.addAll(com.omnimind.pro.ultimate.data.DataStore.initialCats)
                        repo.save(notes, cats)
                        Toast.makeText(context, "Data Wiped", Toast.LENGTH_SHORT).show()
                        showSettings = false
                    }
                )
            }

            Scaffold(
                containerColor = OmniBg,
                topBar = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top=15.dp, start=20.dp, end=20.dp, bottom=10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("OMNIMIND", color = OmniText, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 2.sp)
                        IconButton(onClick = { showSettings = true }) {
                            Icon(Icons.Default.Settings, "Settings", tint = OmniTextDim)
                        }
                    }
                },
                bottomBar = {
                    NavigationBar(containerColor = OmniPanel) {
                        val items = listOf(
                            "Vault" to Icons.Default.Home,
                            "Add" to Icons.Default.Add,
                            "Review" to Icons.Default.Refresh,
                            "Map" to Icons.Default.Info
                        )
                        items.forEach { (label, icon) ->
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = label, tint = if(screen==label) OmniAccent else OmniText) },
                                label = { Text(label, color = if(screen==label) OmniAccent else OmniText) },
                                selected = screen == label,
                                onClick = { screen = label },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = OmniPanel)
                            )
                        }
                    }
                }
            ) { pad ->
                Box(modifier = Modifier.padding(pad)) {
                    when(screen) {
                        "Vault" -> VaultScreen(notes, cats) { repo.save(notes, cats) }
                        "Add" -> AddScreen(cats) { n ->
                            notes.add(0, n)
                            repo.save(notes, cats)
                            Toast.makeText(context, "Locked into Vault", Toast.LENGTH_SHORT).show()
                            screen = "Vault"
                        }
                        "Review" -> ReviewScreen(notes, cats)
                        "Map" -> MindMapScreen(cats, notes)
                    }
                }
            }
        }
    }

    private fun sendNotification(context: Context, text: String) {
        try {
            val builder = NotificationCompat.Builder(context, "tasks_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon as fallback
                .setContentTitle("Task Due")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                 if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notify(System.currentTimeMillis().toInt(), builder.build())
                }
            }
        } catch(e: Exception) { Log.e("Notif", "Error sending", e) }
    }
}
