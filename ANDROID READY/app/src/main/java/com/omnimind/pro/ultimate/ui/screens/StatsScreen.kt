package com.omnimind.pro.ultimate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.omnimind.pro.ultimate.data.Note
import com.omnimind.pro.ultimate.ui.theme.OmniText

@Composable
fun StatsScreen(notes: List<Note>) {
    Column {
        Text("Total Notes: ${notes.size}", color = OmniText)
        Text("Pending Tasks: ${notes.count { it.cat == "Tasks" && !it.notified }}", color = OmniText)
    }
}
