package com.notes.repo

import api.AppServices
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Class which handles data synchronization between remote and local datastore
 */
internal class RemoteRepository() {

    constructor(storeServices: List<AbstractStorageService>) : this() {
        this.storeServices = storeServices
    }

    private var storeServices: List<AbstractStorageService>? = null

    private val protectMetadataUpdate = Mutex()

    private fun getServices(): List<AbstractStorageService> {
        if (storeServices == null) {
            storeServices = AppServices.getStoreServices()
        }
        val services = mutableListOf<AbstractStorageService>()
        storeServices?.forEach {
            if (it.canUse) services.add(it)
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

                Platform().logger.logi("RemoteRepository::saveNote() to '${service.key}'...")

                updateMetadata(dataStore = service, note = note, pendingUpdate = true)

                val result = service.store(Document(data = note.content, name = note.id.toString()))

                if (result) {
                    updateMetadata(dataStore = service, note = note, pendingUpdate = false)
                } else {
                    Platform().logger.loge("RemoteRepository::saveNote() failed for '${service.key}'")
                }
            }

        }
    }

    // TODO: Think if we can return data one by one but not all at once
    // So if we can use Channel instead of list
    suspend fun fetchCopy(): List<Notes> {
        val notesFound = mutableListOf<Notes>()
        // Merge results from several sources
        // Ideally it should have exact list but do check a merge for safety
        val services = getServices()
        for (service in services) {
            val notesList = service.fetchAll().map { document ->
                Notes(id = document.name.toLong(), content = document.data)
            }
            for (item in notesList) {
                if (notesFound.find { note -> item.id == note.id } == null) {
                    notesFound.add(item)
                }
            }
        }
        return notesFound
    }

    suspend fun fetch(forceOverride: Boolean = false, scope: CoroutineScope) {
        scope.launch {
            Platform().logger.logi("RemoteRepository::fetch()")
            val foundNotes = fetchCopy()
            Platform().logger.logi("RemoteRepository::fetch() size = ${foundNotes.size}")
            process(foundNotes, forceOverride)
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

        scope.launch {

            Platform().logger.logi("RemoteRepository::delete: started")

            setDeleteLocally(note)

            val jobs = mutableListOf<Job>()

            val services = getServices()
            for (service in services) {

                val job = scope.launch {

                    Platform().logger.logi("RemoteRepository::delete: note = '${note.id}' for '${service.key}'")

                    updateMetadata(dataStore = service, note = note, pendingDelete = true)

                    val result = service.delete(note.id.toString())
                    if (result) {
                        updateMetadata(dataStore = service, note = note, pendingDelete = false)
                        Platform().logger.logi(
                            "RemoteRepository::delete: deleted note = '${note.id}' for '${service.key}'"
                        )
                    } else {
                        Platform().logger.loge("RemoteRepository::delete: failed for '${service.key}', note = '${note.id}'")
                    }

                }

                jobs.add(job)
            }

            jobs.joinAll()

            deleteNoteLocally(note)

            Platform().logger.logi("RemoteRepository::delete: done")

        }

    }

    private suspend fun deleteNoteLocally(note: Notes) {

        val db = LocalNoteDatabase.access()
        val metaDb = LocalNoteDatabase.accessNoteMetadata()

        val noteInfo = db.getNoteWithMetadata(note.id).first()

        if (noteInfo != null && noteInfo.metadataId != null) {

            val metadata = metaDb.getMetadata(noteInfo.metadataId!!).first()

            // Can delete locally
            if (metadata != null && !metadata.isPendingDeletionOnRemote() && metadata.pendingDelete) {
                // Deliberately set default value, only uid is important
                // as it is used to select the record
                val noteEntity = NoteEntity(uid = note.id)
                LocalNoteDatabase.access().deleteNote(noteEntity)
                Platform().logger.logi("RemoteRepository::deleteNoteLocally() deleted locally = ${note.id}")
            }
        }
    }

    // We are going to update local db with data fetched from remote.
    private suspend fun process(notes: List<Notes>, forceOverride: Boolean) = coroutineScope {

        Platform().logger.logi("RemoteRepository::process()")

        for (note in notes) {

            launch {

                val db = LocalNoteDatabase.access()
                val metadataDb = LocalNoteDatabase.accessNoteMetadata()

                val curr = db.getNote(note.id).first()
                if (curr == null) {

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

                    Platform().logger.logi("RemoteRepository::process() added note = $id, meta id = $metaId")

                } else if (forceOverride) {
                    // Assuming we don't need to change metadata
                    db.updateNote(note.toEntity(setId = true))
                    Platform().logger.logi("RemoteRepository::process() overridden note = ${curr.uid}")
                } else {
                    Platform().logger.logi("RemoteRepository::process() no-op, already have note = ${curr.uid}")
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

        protectMetadataUpdate.withLock {

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
                        "RemoteRepository::updateMetadata() updated meta id = '${currMetadata.uid}', "
                                + "note id = '${currMetadata.original}' for '${dataStore.key}'"
                    )
                } else {
                    Platform().logger.loge(
                        "RemoteRepository::updateMetadata() absent for '${dataStore.key}'"
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

                Platform().logger.logi("RemoteRepository::updateMetadata() added new metadata = " +
                        "'$id' for ${dataStore.key}")

            }

        }

    }

    private suspend fun setDeleteLocally(note: Notes) {
        val db = LocalNoteDatabase.access()
        val metaDb = LocalNoteDatabase.accessNoteMetadata()

        val noteInfo = db.getNoteWithMetadata(note.id).first()

        if (noteInfo != null && noteInfo.metadataId != null) {

            val currMetadata = metaDb.getMetadata(noteInfo.metadataId!!).first()
            if (currMetadata != null) {
                metaDb.updateMetadata(currMetadata.copy(pendingDelete = true))
                Platform().logger.logi("RemoteRepository::setDeleteLocally() set for ${note.id}")
            }

        }
    }

    private suspend fun logMetadataState(note: Notes, serviceName: String) {

        val db = LocalNoteDatabase.access()
        val metaDb = LocalNoteDatabase.accessNoteMetadata()

        val noteInfo = db.getNoteWithMetadata(note.id).first()

        if (noteInfo != null && noteInfo.metadataId != null) {

            val currMetadata = metaDb.getMetadata(noteInfo.metadataId!!).first()

            if (currMetadata != null) {
                Platform().logger.logi("RemoteRepository::logMetadataState() for '${note.id}' " +
                        "service '${serviceName}' - '${currMetadata.metadata}'")
            }
        }

    }

}
