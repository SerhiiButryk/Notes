package com.notes.db.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers

@Database(entities = [MetadataEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun metadataDao(): NoteMetadataDao
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<NoteDatabase>
): NoteDatabase {
    return builder
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}