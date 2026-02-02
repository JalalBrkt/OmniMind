package com.omnimind.pro.final

data class Note(
    val id: Long = System.currentTimeMillis(),
    var txt: String,
    var cat: String,
    var pinned: Boolean = false,
    var due: String? = null,
    var notified: Boolean = false
)
