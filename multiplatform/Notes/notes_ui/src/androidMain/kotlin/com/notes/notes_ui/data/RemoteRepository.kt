package com.notes.notes_ui.data

import com.notes.api.PlatformAPIs.logger
import com.notes.api.data.Notes
import com.notes.api.provideDataStoreService
import com.notes.data.LocalNoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RemoteRepository {
    private val remoteDataStore = provideDataStoreService()

    fun saveNote(
        scope: CoroutineScope,
        note: Notes,
    ) {
        scope.launch {
            val result = remoteDataStore.store(value = note.content, name = note.id.toString())
            // Updated so 'pendingUpdate' should be false now
            if (result) {
                val db = LocalNoteDatabase.access()
                val data = db.getNote(note.id).first()
                db.updateNote(data!!.copy(pendingUpdate = false))
                logger.logi("RemoteRepository::saveNote() set pending 'false' for = ${note.id}")
            }
        }
    }

    fun fetchNotes(scope: CoroutineScope) {
        scope.launch {
            val list = remoteDataStore.fetchAll()
            processNotes(list)
        }
    }

    // Update remote db if it's needed
    fun updateIfNeeded(scope: CoroutineScope) {
        scope.launch {
            val db = LocalNoteDatabase.access()
            val data = db.getNotes().first()
            for (note in data) {
                launch {
                    if (note.pendingUpdate) {
                        saveNote(this, note.toNote())
                    }
                }
            }
        }
    }

    fun delete(
        scope: CoroutineScope,
        note: Notes,
    ) {
        scope.launch {
            // TODO We should handle result !!!
            remoteDataStore.delete(note.id.toString())
        }
    }

    // We are going to store notes to local db if they are not in it already.
    // Application reads from db and retrieves the most recent user notes.
    private suspend fun processNotes(notes: List<Notes>) =
        coroutineScope {
            for (note in notes) {
                launch {
                    val db = LocalNoteDatabase.access()
                    val data = db.getNote(note.id).first()
                    if (data == null) {
                        // Store note in the local db
                        val newNote = note.copy(pendingUpdate = false)
                        db.insertNote(newNote.toEntity())
                        logger.logi("RemoteRepository::processNotes() added to local store note = ${note.id}")
                    } else {
                        logger.logi("RemoteRepository::processNotes() already have note = ${note.id}")
                    }
                }
            }
        }
}
