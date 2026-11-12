package com.notes.db.model

/**
 * Some additional information about user note
 */
data class NoteInfo(
    val noteId: Long,
    val metadata: String?,
    val metadataId: Long?,
)