package com.notes.db

import androidx.room.RoomDatabase
import com.notes.db.impl.MetadataEntity
import com.notes.db.impl.NoteDatabase
import com.notes.db.impl.getRoomDatabase
import com.notes.db.model.NoteMetadata
import kotlinx.coroutines.flow.first

class AppDatabase(builder: RoomDatabase.Builder<NoteDatabase>) {

    private val db = getRoomDatabase(builder)

    suspend fun insert(metadata: NoteMetadata) {
        db.metadataDao().insertMetadata(
            MetadataEntity(
                metadata = metadata.metadata,
                pendingDelete = metadata.pendingDelete,
                original = metadata.noteId,
            )
        )
    }

    suspend fun delete(id: Long) {
        db.metadataDao().deleteMetadata(
            MetadataEntity(
                uid = id,
            )
        )
    }

    suspend fun select(id: Long): NoteMetadata? {
        val metadata = db.metadataDao().getMetadata(id).first()
        return if (metadata == null) {
            null
        } else {
            NoteMetadata(
                metadata = metadata.metadata,
                id = metadata.uid,
                pendingDelete = metadata.pendingDelete,
                noteId = metadata.original,
            )
        }
    }

    suspend fun update(metadata: NoteMetadata) {
        db.metadataDao().updateMetadata(
            MetadataEntity(
                uid = metadata.id,
                metadata = metadata.metadata,
                pendingDelete = metadata.pendingDelete,
                original = metadata.noteId,
            )
        )
    }

    suspend fun fetch(): List<NoteMetadata> {
        val list = db.metadataDao().getAllMetadata().first()
        return list.map { item ->
            NoteMetadata(
                id = item.uid,
                metadata = item.metadata,
                pendingDelete = item.pendingDelete,
                noteId = item.original,
            )
        }
    }

    fun close() {
        db.close()
    }

}