package com.omnimind.pro.ultimate.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class Repository(private val context: Context) {
    private val gson = Gson()
    private val file = File(context.filesDir, "omnimind_data.json")

    data class Wrapper(val notes: List<Note>, val cats: List<Category>)

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
}
