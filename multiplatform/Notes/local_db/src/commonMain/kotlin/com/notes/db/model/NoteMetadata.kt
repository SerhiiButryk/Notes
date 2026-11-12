package com.notes.db.model

data class NoteMetadata(
    val id: Long = 0,
    val metadata: String = "",
    val pendingDelete: Boolean = false,
    val noteId: Long? = null,
)