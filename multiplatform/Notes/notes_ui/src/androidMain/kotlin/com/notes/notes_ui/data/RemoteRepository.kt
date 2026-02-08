package com.notes.notes_ui.data

import api.PlatformAPIs.logger
import api.data.AbstractStorageService
import api.data.Document
import api.data.Notes
import com.notes.data.LocalNoteDatabase
import com.notes.data.NoteEntity
import com.notes.data.NotesMetadataEntity
import com.notes.data.json.isPendingDeletionOnRemote
import com.notes.data.json.isPendingUpdateOnRemote
import com.notes.data.json.update
import com.notes.data.json.updateForDatastore
import com.notes.data.toEntity
import com.notes.data.toNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Class which handles data synchronization between remote and local datastore
 */
class RemoteRepository(remoteDataStore: List<AbstractStorageService>) {

    private val _allServicesList: List<AbstractStorageService> = remoteDataStore

    private fun getServices(): List<AbstractStorageService> {
        val services = mutableListOf<AbstractStorageService>()
        for (service in _allServicesList) {
            if (service.canUse)
                services.add(service)
        }
        logger.logi("RemoteRepository::getServices() available services = '$services'")
        return services
    }

    fun saveNote(
        scope: CoroutineScope,
        note: Notes,
    ) {
        scope.launch {

            for (dataStore in getServices()) {

                logger.logi("RemoteRepository::saveNote() to '${dataStore.name}'...")

                updateMetadata(dataStore = dataStore, note = note, pendingUpdate = true)

                val result =
                    dataStore.store(Document(data = note.content, name = note.id.toString()))

                if (result) {
                    updateMetadata(dataStore = dataStore, note = note, pendingUpdate = false)
                } else {
                    logger.loge("RemoteRepository::saveNote() failed for '${dataStore.name}'")
                }
            }

        }
    }

    fun fetchNotes(scope: CoroutineScope) {
        logger.logi("RemoteRepository::fetchNotes()")
        scope.launch {
            val notesFound = mutableListOf<Notes>()
            // Merge results from several sources
            // Ideally it should have exact list but do check a merge for safety
            for (dataStore in getServices()) {
                val result = dataStore.fetchAll().map { document ->
                    Notes(id = document.name.toLong(), content = document.data)
                }
                for (item in result) {
                    if (notesFound.find { note -> item.id == note.id } == null) {
                        notesFound.add(item)
                    }
                }
            }
            logger.logi("RemoteRepository::fetchNotes() got ${notesFound.size}")
            processNotes(notesFound)
        }
    }

    // Start checking if we need to process anything

    fun updateIfNeeded(scope: CoroutineScope) {
        scope.launch {

            val metaDb = LocalNoteDatabase.accessNoteMetadata()
            val currMetadata = metaDb.getAllMetadata().first()

            for (metadata in currMetadata) {

                launch {

                    val db = LocalNoteDatabase.access()
                    val note = db.getNote(metadata.original!!).first()

                    if (note != null) {

                        val noteLocal = note.toNote()

                        if (metadata.isPendingUpdateOnRemote()) {
                            saveNote(this, noteLocal)
                        }

                        if (metadata.isPendingDeletionOnRemote()) {
                            delete(this, noteLocal)
                        }
                    }

                }
            }
        }
    }

    fun delete(
        scope: CoroutineScope,
        note: Notes,
    ) {

        logger.logi("RemoteRepository::delete() note = '${note.id}'")

        scope.launch {

            for (dataStore in getServices()) {

                updateMetadata(dataStore = dataStore, note = note, pendingDelete = true)

                val result = dataStore.delete(note.id.toString())
                if (result) {
                    updateMetadata(dataStore = dataStore, note = note, pendingDelete = false)
                    logger.logi("RemoteRepository::delete() deleted = '${note.id}' for '${dataStore.name}'")
                } else {
                    logger.loge("RemoteRepository::delete() failed for '${dataStore.name}'")
                }

            }

            checkIfNeedDeleteNoteLocally(note)

            logger.logi("RemoteRepository::delete() done")
        }
    }

    private suspend fun checkIfNeedDeleteNoteLocally(note: Notes) {

        val db = LocalNoteDatabase.access()
        val metaDb = LocalNoteDatabase.accessNoteMetadata()

        val noteInfo = db.getNoteWithMetadata(note.id).first()

        if (noteInfo != null && noteInfo.metadataId != null) {

            val metadata = metaDb.getMetadata(noteInfo.metadataId!!).first()

            // Can delete locally
            if (metadata != null
                && !metadata.isPendingDeletionOnRemote()
                && metadata.pendingDelete) {
                // Deliberately set default value, only uid is important
                // as it is used to select the record
                val noteEntity = NoteEntity(uid = note.id)
                LocalNoteDatabase.access().deleteNote(noteEntity)
                logger.logi("RemoteRepository::checkIfNeedDeleteNoteLocally() deleted locally = ${note.id}")
            }
        }
    }

    // We are going to store notes to local db if they are not stored already.
    // Application reads from local db to get the most recent data.
    private suspend fun processNotes(notes: List<Notes>) = coroutineScope {

        logger.logi("RemoteRepository::processNotes()")

        for (note in notes) {

            launch {

                val db = LocalNoteDatabase.access()
                val metadataDb = LocalNoteDatabase.accessNoteMetadata()

                val data = db.getNote(note.id).first()
                if (data == null) {

                    // Store note and its metadata records

                    val id = db.insertNote(note.toEntity())

                    val metadata = NotesMetadataEntity(original = id, metadata = "")

                    val newMetadata = metadata.update(
                        pendingUpdateFirebase = false,
                        pendingUpdateGoogle = false,
                        pendingDeleteGoogle = false,
                        pendingDeleteFirebase = false
                    )

                    val metaId = metadataDb.insertMetadata(metadata.copy(metadata = newMetadata))

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
        dataStore: AbstractStorageService,
        note: Notes,
        pendingUpdate: Boolean? = null,
        pendingDelete: Boolean? = null
    ) {

        val db = LocalNoteDatabase.access()
        val metaDb = LocalNoteDatabase.accessNoteMetadata()

        val noteInfo = db.getNoteWithMetadata(note.id).first()

        if (noteInfo != null && noteInfo.metadataId != null) {

            // Update current

            val currMetadata = metaDb.getMetadata(noteInfo.metadataId!!).first()

            if (currMetadata != null) {

                val newMetadata = currMetadata.updateForDatastore(
                    dataStore = dataStore,
                    pendingDelete = pendingDelete,
                    pendingUpdate = pendingUpdate
                )

                metaDb.updateMetadata(currMetadata.copy(metadata = newMetadata))

                logger.logi(
                    "RemoteRepository::updateMetadata() updated = '${currMetadata.uid}' for '${dataStore.name}'"
                )
            } else {
                logger.loge(
                    "RemoteRepository::updateMetadata() absent for '${dataStore.name}'"
                )
            }

        } else {

            // If there are no metadata then create it

            val newMetadata = NotesMetadataEntity(
                original = note.id,
                metadata = "",
            )

            val updated = newMetadata.updateForDatastore(
                dataStore = dataStore,
                pendingDelete = pendingDelete,
                pendingUpdate = pendingUpdate
            )

            val id = metaDb.insertMetadata(newMetadata.copy(metadata = updated))

            logger.logi(
                "RemoteRepository::updateMetadata() added new metadata = '$id' for ${dataStore.name}"
            )

        }
    }
}
