package com.omnimind.pro.ultimate

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.omnimind.pro.ultimate.data.Category
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.data.Repository
import com.omnimind.pro.ultimate.ui.screens.*
import com.omnimind.pro.ultimate.ui.theme.OmniAccent
import com.omnimind.pro.ultimate.ui.theme.OmniBg
import com.omnimind.pro.ultimate.ui.theme.OmniPanel
import com.omnimind.pro.ultimate.ui.theme.OmniText
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

        val repo = Repository(this)
        val (loadedNotes, loadedCats) = repo.load()

        val notes = mutableStateListOf<Note>().apply { addAll(loadedNotes) }
        val cats = mutableStateListOf<Category>().apply { addAll(loadedCats) }

        setContent {
            var screen by remember { mutableStateOf("Vault") }
            var showStats by remember { mutableStateOf(false) }
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

            Scaffold(
                containerColor = OmniBg,
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
