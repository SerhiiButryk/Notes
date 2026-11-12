package com.notes.db.impl

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User local note additional data
 */
@Entity(
    tableName = "notes_metadata",
)
data class MetadataEntity(
    @PrimaryKey(autoGenerate = true)
    // Entity id
    val uid: Long = 0,
    // Additional data in json format
    @ColumnInfo("meta_data") val metadata: String = "",
    // Whether deletion should be done locally
    @ColumnInfo("pending_delete") val pendingDelete: Boolean = false,
    // Id of the user note
    val original: Long? = null,
)
