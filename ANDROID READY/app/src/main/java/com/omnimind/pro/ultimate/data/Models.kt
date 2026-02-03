package com.omnimind.pro.ultimate.data

data class Note(
    val id: Long = System.currentTimeMillis(),
    var txt: String,
    var cat: String,
    var pinned: Boolean = false,
    var due: String? = null,
    var notified: Boolean = false
)

data class Category(
    var n: String,
    var c: String
)

object DataStore {
    val initialCats = listOf(
        Category("General", "#64748b"),
        Category("Wisdom", "#fbbf24"),
        Category("Code", "#10b981"),
        Category("Tasks", "#ef4444")
    )
}
