package com.notes.notes_ui.data

import api.PlatformAPIs.logger
import api.StorageService
import api.data.Notes
import com.notes.data.LocalNoteDatabase
import com.notes.data.NoteEntity
import com.notes.data.NotesMetadataEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Class which handles data synchronization between remote and locale datastore
 */
class RemoteRepository(private val remoteDataStore: StorageService) {

    fun saveNote(
        scope: CoroutineScope,
        note: Notes,
    ) {
        scope.launch {

            logger.logi("RemoteRepository::saveNote()")

            updateMetadata(note = note, pendingUpdate = true)

            val result = remoteDataStore.store(value = note.content, name = note.id.toString())

            if (result) {
                updateMetadata(note = note, pendingUpdate = false)
            } else {
                logger.loge("RemoteRepository::saveNote() failed to save in remote")
            }

        }
    }

    fun fetchNotes(scope: CoroutineScope) {
        logger.logi("RemoteRepository::fetchNotes()")
        scope.launch {
            val list = remoteDataStore.fetchAll()
            processNotes(list)
        }
    }

    // Start checking if we need to process anything

    fun updateIfNeeded(scope: CoroutineScope) {
        scope.launch {

            val metaDb = LocalNoteDatabase.accessNoteMetadata()
            val currMetadata = metaDb.getAllMetadata().first()

            for (metadata in currMetadata) {
                launch {

                    if (metadata.pendingUpdate) {
                        val db = LocalNoteDatabase.access()
                        val note = db.getNote(metadata.original!!).first()
                        saveNote(this, note!!.toNote())
                    }

                    if (metadata.pendingDelete) {
                        val db = LocalNoteDatabase.access()
                        val note = db.getNote(metadata.original!!).first()
                        delete(this, note!!.toNote())
                    }

                }
            }
        }
    }

    fun delete(
        scope: CoroutineScope,
        note: Notes,
    ) {

        logger.logi("RemoteRepository::delete() note id = ${note.id}")

        scope.launch {

            // TODO: Might need to handle additional
            // We shouldn't return notes which are pending deletion

            updateMetadata(note, pendingDelete = true)

            val result = remoteDataStore.delete(note.id.toString())

            if (result) {

                val db = LocalNoteDatabase.access()
                val noteInfo = db.getNoteWithMetadata(note.id).first()

                if (noteInfo != null && noteInfo.metadataId != null) {
                    val metaDb = LocalNoteDatabase.accessNoteMetadata()
                    val entity = NotesMetadataEntity(uid = noteInfo.metadataId!!)
                    metaDb.deleteMetadata(entity)
                    logger.logi("RemoteRepository::delete() note metadata is deleted = ${noteInfo.noteId}")
                }

                // Deliberately set default value, only uid is important
                // as it is used to select the record
                val noteEntity = NoteEntity(uid = note.id)
                db.deleteNote(noteEntity)

                logger.logi("RemoteRepository::delete() local note is deleted = ${note.id}")

            } else {
                logger.loge("RemoteRepository::delete() failed to delete in remote")
            }

            logger.logi("RemoteRepository::delete() done")
        }
    }

    // We are going to store notes to local db if they are not stored already.
    // Application reads from local db to get the most recent data.
    private suspend fun processNotes(notes: List<Notes>) = coroutineScope {

        logger.logi("RemoteRepository::processNotes()")

        for (note in notes) {

            launch {
                val db = LocalNoteDatabase.access()
                val data = db.getNote(note.id).first()
                if (data == null) {

                    // Store note and its metadata records

                    val id = db.insertNote(note.toEntity())

                    val metadataDb = LocalNoteDatabase.accessNoteMetadata()

                    val metadata = NotesMetadataEntity(pendingUpdate = false, original = id)
                    val metaId = metadataDb.insertMetadata(metadata)

                    logger.logi(
                        "RemoteRepository::processNotes() " +
                                "added to local store note = $id, meta id = $metaId"
                    )
                } else {
                    logger.logi("RemoteRepository::processNotes() already have note = ${data.uid}")
                }
            }
        }

    }

    private suspend fun updateMetadata(
        note: Notes,
        pendingUpdate: Boolean = false,
        pendingDelete: Boolean = false
    ) {

        val db = LocalNoteDatabase.access()
        val noteInfo = db.getNoteWithMetadata(note.id).first()

        if (noteInfo != null && noteInfo.metadataId != null) {

            // Update current

            val metaDb = LocalNoteDatabase.accessNoteMetadata()
            val currMetadata = metaDb.getMetadata(noteInfo.metadataId!!).first()

            if (currMetadata != null) {

                metaDb.updateMetadata(
                    currMetadata.copy(
                        pendingUpdate = pendingUpdate,
                        pendingDelete = pendingDelete
                    )
                )

                logger.logi(
                    "RemoteRepository::updateMetadata() updated = ${currMetadata.uid}"
                )
            }

        } else {

            // If there are no metadata then create it

            val metaDb = LocalNoteDatabase.accessNoteMetadata()

            val newMetadata = NotesMetadataEntity(
                original = note.id,
                pendingUpdate = pendingUpdate,
                pendingDelete = pendingDelete
            )

            val id = metaDb.insertMetadata(newMetadata)

            logger.logi(
                "RemoteRepository::updateMetadata() added new = $id"
            )

        }
    }
}
