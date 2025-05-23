package com.notes.data.models

/**
 * Additional note data
 */
data class NoteInfo(
    val noteId: Long,
    val pendingUpdate: Boolean?,
    val pendingDelete: Boolean?,
    val metadataId: Long?,
)