package com.omnimind.pro.final

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import java.io.File

data class AppData(
    val notes: MutableList<Note> = mutableListOf(),
    val cats: MutableList<Category> = mutableListOf(
        Category("General", "#64748b"),
        Category("Wisdom", "#fbbf24"),
        Category("Code", "#10b981"),
        Category("Tasks", "#ef4444")
    )
)

class DataRepository(private val context: Context) {
    private val gson = Gson()
    private val fileName = "omnimind_data.json"
    private val file by lazy { File(context.filesDir, fileName) }

    var data by mutableStateOf(AppData())
        private set

    init {
        load()
    }

    fun load() {
        if (file.exists()) {
            try {
                val json = file.readText()
                val loaded = gson.fromJson(json, AppData::class.java) ?: AppData()
                data = loaded
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (data.cats.none { it.n == "Tasks" }) {
            data.cats.add(Category("Tasks", "#ef4444"))
        }
    }

    fun save() {
        try {
            val json = gson.toJson(data)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addNote(note: Note) {
        val newNotes = data.notes.toMutableList()
        newNotes.add(0, note)
        data = data.copy(notes = newNotes)
        save()
    }

    fun updateNote(note: Note) {
        val newNotes = data.notes.toMutableList()
        val index = newNotes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            newNotes[index] = note
            data = data.copy(notes = newNotes)
            save()
        }
    }

    fun deleteNote(id: Long) {
        val newNotes = data.notes.toMutableList()
        newNotes.removeAll { it.id == id }
        data = data.copy(notes = newNotes)
        save()
    }

    fun addCategory(cat: Category) {
        if (data.cats.any { it.n == cat.n }) return
        val newCats = data.cats.toMutableList()
        newCats.add(cat)
        data = data.copy(cats = newCats)
        save()
    }

    fun deleteCategory(name: String) {
        if (name == "General" || name == "Tasks") return
        val newCats = data.cats.toMutableList()
        newCats.removeAll { it.n == name }
        // Move notes to General
        val newNotes = data.notes.toMutableList()
        newNotes.forEach { if (it.cat == name) it.cat = "General" }

        data = data.copy(cats = newCats, notes = newNotes)
        save()
    }

    fun wipe() {
        data = AppData()
        save()
    }

    fun importData(json: String) {
        try {
            val imported = gson.fromJson(json, AppData::class.java)
            if (imported != null && imported.notes != null) {
                data = imported
                save()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
