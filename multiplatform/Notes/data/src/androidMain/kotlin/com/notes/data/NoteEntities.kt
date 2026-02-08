package com.notes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * User local note data
 */
@Entity(tableName = "notes_data")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo("user_id") val userId: String = "",
    // Html content
    @ColumnInfo("note_content") val content: String = "",
    @ColumnInfo("time") val time: String = ""
)

/**
 * User local note additional data
 */
@Entity(
    tableName = "notes_metadata",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["uid"],
            childColumns = ["original"],
            onDelete = ForeignKey.CASCADE // Delete metadata if note is deleted locally
        )
    ]
)
data class NotesMetadataEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    // Additional data in json format
    @ColumnInfo("meta_data") val metadata: String = "",
    // Whether deletion should be done locally
    @ColumnInfo("pending_delete") val pendingDelete: Boolean = false,
    val original: Long? = null
)
