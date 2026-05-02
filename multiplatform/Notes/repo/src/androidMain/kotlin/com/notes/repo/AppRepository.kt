package com.notes.repo

import api.AppServices
import api.Platform
import api.data.AbstractStorageService
import api.data.Notes
import api.data.isEqualTo
import api.repo.Repository
import com.notes.db.LocalNoteDatabase
import com.notes.db.isAllInSyncWithRemote
import com.notes.db.json.isPendingDeletionOnRemote
import com.notes.db.toEntity
import com.notes.db.toNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.collections.mutableListOf

class AppRepository private constructor(
    private val remoteRepository: RemoteRepository
) : Repository {

    companion object {

        // Factory to create this repository

        fun create(): AppRepository {
            return AppRepository(
                RemoteRepository(AppServices.dataStoreService)
            )
        }

        fun create(services: List<AbstractStorageService>): AppRepository {
            return AppRepository(
                RemoteRepository(services)
            )
        }
    }

    private val coroutineContext = SupervisorJob() + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)
    private val filesManager = FilesManager()

    private var cachedLocalNotes: List<Notes> = emptyList()

    override fun getNotes(): Flow<List<Notes>> =
        flow {
            Platform().logger.logi("OfflineRepository::getNotes()")

            // TODO: Might do this periodically in Work manager or something else
            // Trigger fetch from remote server
            remoteRepository.fetch(scope = scope)

            val db = LocalNoteDatabase.accessNoteMetadata()

            // Trigger load from local db
            // 1. Load note mata data (it's some additional info)
            // 2. Check it and load a note

            db.getAllMetadata().collect { metadataList ->

                val list = mutableListOf<Notes>()

                metadataList.forEach { metadata ->

                    val noteId = metadata.original

                    if (!metadata.isPendingDeletionOnRemote() && !metadata.pendingDelete) {
                        val db = LocalNoteDatabase.access()
                        val note = db.getNote(noteId!!).first()!!.toNote()
                        list.add(note)
                    }

                }

                cachedLocalNotes = list

                emit(list)
            }

        }.flowOn(Dispatchers.IO)

    override fun getNotes(id: Long): Flow<Notes?> =
        flow {
            Platform().logger.logi("OfflineRepository::getNotes(id=$id)")

            LocalNoteDatabase
                .access()
                .getNote(id)
                .map { it?.toNote() }
                .collect {
                    emit(it)
                }

        }.flowOn(Dispatchers.IO)

    override fun saveNote(
        note: Notes,
        onNewAdded: suspend (Long) -> Unit,
    ) = saveNote(scope = scope, note = note, onNewAdded = onNewAdded)

    fun saveNote(
        scope: CoroutineScope,
        note: Notes,
        onNewAdded: suspend (Long) -> Unit,
    ) {
        scope.launch {
            val db = LocalNoteDatabase.access()
            val newNote = db.getNote(note.id).first() == null
            if (newNote) {
                val id = db.insertNote(note.toEntity(setId = false /* Use auto-increment */))
                onNewAdded(id)
                Platform().logger.logi("OfflineRepository::saveNote($id) new record is added locally")
                remoteRepository.saveNote(scope, note.copy(id = id))
            } else {
                db.updateNote(note.toEntity())
                Platform().logger.logi("OfflineRepository::saveNote(${note.id}) is updated locally")
                remoteRepository.saveNote(scope, note)
            }
        }
    }

    override fun deleteNote(
        note: Notes,
        callback: (Long) -> Unit,
    ) = deleteNote(scope = scope, note = note, callback = callback)

    fun deleteNote(
        scope: CoroutineScope,
        note: Notes,
        callback: (Long) -> Unit,
    ) {
        scope.launch {
            Platform().logger.logi("OfflineRepository::delete(${note.id})")
            // Trigger deletion
            remoteRepository.delete(scope, note)
            // Notify UI
            callback(note.id)
        }
    }

    override suspend fun canChangePassword(): Boolean {

        if (!Platform().netStateManager.isNetworkAvailable()) {
            Platform().logger.loge("OfflineRepository::canChangePassword() no network")
            return false
        }

        if (!isDataInSync()) {
            Platform().logger.loge("OfflineRepository::canChangePassword() " +
                    "local data is not up-to-date with remote")
            return false
        }

        val remoteNotes = remoteRepository.fetchCopy()
        if (!remoteNotes.isEqualTo(cachedLocalNotes)) {
            Platform().logger.loge("OfflineRepository::canChangePassword() " +
                    "remote data is not up-to-date with local data")
            return false
        }

        // Create backup files
        val result = filesManager.saveToDisk(remoteNotes)
        Platform().logger.logi("OfflineRepository::canChangePassword: $result")
        return result
    }

    override suspend fun onPasswordChanged() {
        Platform().logger.logi("OfflineRepository::onPasswordChanged()")

        coroutineScope {
            val notesFromDisk = filesManager.readFromDisk()
            for (note in notesFromDisk) {
                remoteRepository.saveNote(scope = this, note = note)
            }
        }

        clearLocalAppStorage()

        coroutineScope {
            // This should get our app in sync when password has changed
            remoteRepository.fetch(scope = this)
        }

        Platform().logger.logi("OfflineRepository::onPasswordChanged() done")
    }

    override suspend fun clearLocalAppStorage() {
        // Will be clearing database completely
        LocalNoteDatabase.access().delete()
    }

    override suspend fun isDataInSync() = isAllInSyncWithRemote()

    fun syncData(newScope: CoroutineScope? = null) =
        remoteRepository.updateIfNeeded(newScope ?: scope)

    override fun clear() {
        Platform().logger.logi("OfflineRepository::clear()")
        scope.cancel()
    }

}
