package com.notes.repo

import api.AppService
import api.AppServices
import api.Platform
import api.data.AbstractStorageService
import api.data.Document
import api.data.Notes
import com.notes.db.ClientSyncManager
import com.notes.db.OnAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.File

/**
 * Class which handles data synchronization between remote and local datastore
 */
class RemoteRepository(
    private val syncManager: ClientSyncManager
) {

    constructor(
        storeServices: List<AbstractStorageService>,
        syncManager: ClientSyncManager
    ) : this(syncManager) {
        this.storeServices = storeServices
    }

    private var storeServices: List<AbstractStorageService>? = null

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
                Platform().logger.logi("RemoteRepository::saveNote() note '${note.id}' to '${service.key}'...")

                var noteToSave = note

                // TODO: This is a corner case maybe revisit later.
                // When user creates a new note a name is not set and here
                // we are trying to set it
                if (note.id == -1L) {
                    val name = service.selectDocName() ?: return@launch
                    noteToSave = note.copy(id = name)
                }

                syncManager.updateMetadata(dataStore = service, note = noteToSave, pendingUpdate = true)

                val result = service.store(Document(data = noteToSave.content, name = noteToSave.id.toString()))

                if (result) {
                    syncManager.updateMetadata(dataStore = service, note = noteToSave, pendingUpdate = false)
                } else {
                    Platform().logger.loge("RemoteRepository::saveNote() failed note '${noteToSave.id}' for '${service.key}'")
                }
            }
        }
    }

    // TODO: Think if we can return data one by one but not all at once
    // So if we can make a good use of Channel
    suspend fun fetchCopy(): List<Notes> {
        val notesFound = mutableListOf<Notes>()
        // Merge results from several sources
        // Ideally it should have exact list but do check a merge for safety
        val services = getServices()
        for (service in services) {
            val notesList =
                service.fetchAll().map { document ->
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

    fun fetch(
        forceOverride: Boolean = false,
        scope: CoroutineScope,
    ): Job {
        return scope.launch {
            Platform().logger.logi("RemoteRepository::fetch()")
            val foundNotes = fetchCopy()
            Platform().logger.logi("RemoteRepository::fetch() size = ${foundNotes.size}")
            // We are going to update local db with data fetched from remote.
            syncManager.store(
                notes = foundNotes,
                forceOverride = forceOverride,
                scope = scope
            )
        }
    }

    // Start checking if we need to process anything that's not been synced

    fun sync(scope: CoroutineScope) {
        Platform().logger.logi("RemoteRepository::sync()")
        syncManager.sync(scope = scope, action = object : OnAction {
            override fun onDeleteRequired(note: Notes) {
                saveNote(scope = scope, note = note)
            }
            override fun onSaveRequired(note: Notes) {
                delete(scope = scope, note = note)
            }
        })
    }

    fun delete(
        scope: CoroutineScope,
        note: Notes,
    ) {
        scope.launch {
            Platform().logger.logi("RemoteRepository::delete: started")

            syncManager.markPendingDeletion(note)

            val jobs = mutableListOf<Job>()

            val services = getServices()
            for (service in services) {
                val job =
                    scope.launch {
                        Platform().logger.logi("RemoteRepository::delete: note '${note.id}' for '${service.key}'")

                        syncManager.updateMetadata(dataStore = service, note = note, pendingDelete = true)

                        val result = service.delete(Document(name = note.id.toString()))
                        if (result) {
                            syncManager.updateMetadata(dataStore = service, note = note, pendingDelete = false)
                            Platform().logger.logi(
                                "RemoteRepository::delete: deleted note '${note.id}' for '${service.key}'",
                            )
                        } else {
                            Platform().logger.loge("RemoteRepository::delete: failed note '${note.id}' for '${service.key}', note = '${note.id}'")
                        }
                    }

                jobs.add(job)
            }

            jobs.joinAll()

            syncManager.delete(note)

            Platform().logger.logi("RemoteRepository::delete: done")
        }
    }

    suspend fun saveAttachment(file: File): Boolean {
        Platform().logger.logi("RemoteRepository::saveAttachment")
        val services = getServices()
        for (service in services) {
            // Google Drive is only supported
            if (service.key == AppService.GOOGLE_STORAGE) {
                val document = Document(file)
                return service.store(document)
            }
        }
        return false
    }

    suspend fun deleteAttachment(
        scope: CoroutineScope,
        name: String,
    ): Boolean {
        val result =
            scope.async {
                Platform().logger.logi("RemoteRepository::deleteAttachment")
                val services = getServices()
                for (service in services) {
                    // Google Drive is only supported
                    if (service.key == AppService.GOOGLE_STORAGE) {
                        val document = Document(name = name)
                        document.isFile = true
                        return@async service.delete(document)
                    }
                }
                return@async false
            }
        return result.await()
    }
}