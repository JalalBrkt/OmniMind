package com.omnimind.pro.ultimate.data

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import java.io.File

class Repository(private val context: Context) {
    private val gson = Gson()
    private val file = File(context.filesDir, "omnimind_data.json")

    data class Wrapper(val notes: List<Note>, val cats: List<Category>, val type: String = "full", val cat: String? = null)

    fun load(): Pair<MutableList<Note>, MutableList<Category>> {
        if (!file.exists()) return Pair(mutableListOf(), DataStore.initialCats.toMutableList())
        return try {
            val w = gson.fromJson(file.readText(), Wrapper::class.java)
            Pair(w.notes.toMutableList(), w.cats.toMutableList())
        } catch (e: Exception) {
            Pair(mutableListOf(), DataStore.initialCats.toMutableList())
        }
    }

    fun save(notes: List<Note>, cats: List<Category>) {
        file.writeText(gson.toJson(Wrapper(notes, cats)))
    }

    fun exportToUri(uri: Uri, notes: List<Note>, cats: List<Category>, catFilter: String?) {
        try {
            val data = if (catFilter == null || catFilter == "All") {
                Wrapper(notes, cats, type = "full")
            } else {
                Wrapper(notes.filter { it.cat == catFilter }, emptyList(), type = "partial", cat = catFilter)
            }
            val json = gson.toJson(data)
            context.contentResolver.openOutputStream(uri)?.use {
                it.write(json.toByteArray())
            }
        } catch(e: Exception) { e.printStackTrace() }
    }

    // Returns a Wrapper with the merged/replaced data to be applied by the UI
    fun importFromUri(uri: Uri, currentNotes: List<Note>, currentCats: List<Category>): Wrapper? {
        return try {
            context.contentResolver.openInputStream(uri)?.use {
                val json = it.bufferedReader().readText()
                val imported = gson.fromJson(json, Wrapper::class.java)

                if (imported.type == "partial" && imported.cat != null) {
                    // Merge Logic
                    val newNotes = currentNotes.toMutableList()
                    var count = 0
                    imported.notes.forEach { n ->
                        if (newNotes.none { it.id == n.id }) {
                            newNotes.add(0, n)
                            count++
                        }
                    }
                    val newCats = currentCats.toMutableList()
                    if (newCats.none { it.n == imported.cat }) {
                        newCats.add(Category(imported.cat, "#38bdf8"))
                    }
                    Wrapper(newNotes, newCats, type = "merged")
                } else {
                    // Full Replace
                    imported
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
