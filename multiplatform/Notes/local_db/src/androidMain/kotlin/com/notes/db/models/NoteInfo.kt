package com.notes.db.models

/**
 * Additional note data
 */
data class NoteInfo(
    val noteId: Long,
    val metadata: String?,
    val metadataId: Long?,
)