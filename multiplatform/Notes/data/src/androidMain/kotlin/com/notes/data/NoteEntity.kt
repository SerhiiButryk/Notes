package com.notes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_data")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo("user_id") val userId: String,
    // Html content
    @ColumnInfo("note_content") val content: String,
    @ColumnInfo("time") val time: String,
)