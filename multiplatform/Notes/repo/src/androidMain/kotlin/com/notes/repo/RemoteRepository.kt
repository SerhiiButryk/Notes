package com.notes.repo

import api.Platform
import api.data.AbstractStorageService
import api.data.Document
import api.data.Notes
import com.notes.db.LocalNoteDatabase
import com.notes.db.NoteEntity
import com.notes.db.NotesMetadataEntity
import com.notes.db.json.isPendingDeletionOnRemote
import com.notes.db.json.isPendingUpdateOnRemote
import com.notes.db.json.update
import com.notes.db.json.updateForDatastore
import com.notes.db.toEntity
import com.notes.db.toNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Class which handles data synchronization between remote and local datastore
 */
internal class RemoteRepository(private val allServicesList: List<AbstractStorageService>) {

    private fun getServices(): List<AbstractStorageService> {
        val services = mutableListOf<AbstractStorageService>()
        for (service in allServicesList) {
            if (service.canUse)
                services.add(service)
        }
        Platform().logger.logi("RemoteRepository::getServices() available services = '${services.size}'")
        return services
    }

    fun saveNote(
        scope: CoroutineScope,
        note: Notes,
    ) {
        scope.launch {

            val services = getServices()
            for (service in services) {

                Platform().logger.logi("RemoteRepository::saveNote() to '${service.name}'...")

                updateMetadata(dataStore = service, note = note, pendingUpdate = true)

                val result =
                    service.store(Document(data = note.content, name = note.id.toString()))

                if (result) {
                    updateMetadata(dataStore = service, note = note, pendingUpdate = false)
                } else {
                    Platform().logger.loge("RemoteRepository::saveNote() failed for '${service.name}'")
                }
            }

        }
    }

    fun fetchNotes(scope: CoroutineScope) {
        Platform().logger.logi("RemoteRepository::fetchNotes()")
        scope.launch {
            val notesFound = mutableListOf<Notes>()
            // Merge results from several sources
            // Ideally it should have exact list but do check a merge for safety
            val services = getServices()
            for (service in services) {
                val result = service.fetchAll().map { document ->
                    Notes(id = document.name.toLong(), content = document.data)
                }
                for (item in result) {
                    if (notesFound.find { note -> item.id == note.id } == null) {
                        notesFound.add(item)
                    }
                }
            }
            Platform().logger.logi("RemoteRepository::fetchNotes() got ${notesFound.size}")
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

        Platform().logger.logi("RemoteRepository::delete() note = '${note.id}'")

        scope.launch {

            val services = getServices()
            for (service in services) {

                updateMetadata(dataStore = service, note = note, pendingDelete = true)

                val result = service.delete(note.id.toString())
                if (result) {
                    updateMetadata(dataStore = service, note = note, pendingDelete = false)
                    Platform().logger.logi("RemoteRepository::delete() deleted = '${note.id}' for '${service.name}'")
                } else {
                    Platform().logger.loge("RemoteRepository::delete() failed for '${service.name}'")
                }

            }

            checkIfNeedDeleteNoteLocally(note)

            Platform().logger.logi("RemoteRepository::delete() done")
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
                Platform().logger.logi("RemoteRepository::checkIfNeedDeleteNoteLocally() deleted locally = ${note.id}")
            }
        }
    }

    // We are going to store notes to local db if they are not stored already.
    // Application reads from local db to get the most recent data.
    private suspend fun processNotes(notes: List<Notes>) = coroutineScope {

        Platform().logger.logi("RemoteRepository::processNotes()")

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

                    Platform().logger.logi(
                        "RemoteRepository::processNotes() " +
                                "added to local store note = $id, meta id = $metaId"
                    )
                } else {
                    Platform().logger.logi("RemoteRepository::processNotes() already have note = ${data.uid}")
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

                Platform().logger.logi(
                    "RemoteRepository::updateMetadata() updated = '${currMetadata.uid}' for '${dataStore.name}'"
                )
            } else {
                Platform().logger.loge(
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

            Platform().logger
                .logi("RemoteRepository::updateMetadata() added new metadata = '$id' for ${dataStore.name}")

        }
    }
}
