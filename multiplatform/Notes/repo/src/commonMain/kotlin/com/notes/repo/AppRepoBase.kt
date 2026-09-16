package com.notes.repo

import api.Platform
import api.data.Attachments
import api.data.Notes
import api.repo.BaseRepo
import com.notes.db.ClientSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

open class AppRepoBase(
    protected val syncManager: ClientSyncManager,
    protected val remoteRepository: RemoteRepository = RemoteRepository(syncManager),
    // For test support
    scopeOverride: CoroutineScope? = null,
) : BaseRepo(scopeOverride) {

    @Volatile // Make sure that the updated value is visible to all threads
    var cachedLocalNotes: List<Notes> = emptyList()

    @OptIn(DelicateCoroutinesApi::class)
    override fun getNotes(): Flow<List<Notes>> = flow {
        // Trigger sync with server
        syncData()
        // Trigger fetch from remote server
        remoteRepository.fetch(scope = scope)
        // Waiting on some data
        syncManager.notes.collect {
            cachedLocalNotes = it
            emit(it)
        }
    }

    override fun getNotes(id: Long): Flow<Notes?> = flow {
        val notes = syncManager.notes.first()
        notes.forEach { item ->
            if (item.id == id) {
                emit(item)
                return@flow
            }
        }
        emit(null)
    }

    override fun saveNote(
        note: Notes,
        onAdded: suspend (Notes?) -> Unit,
    ) {
        scope.launch {
            var savedNote: Notes? = null
            coroutineScope {
                remoteRepository.saveNote(scope = this, note = note) {
                    savedNote = it
                }
            }
            // Refresh and wait while it completes
            remoteRepository.fetch(scope = this).join()
            onAdded(savedNote)
        }
    }

    override fun deleteNote(
        note: Notes,
        onDeleted: (Long) -> Unit,
    ) {
        Platform().logger.logi("deleteNote() ${note.id}")
        scope.launch {
            coroutineScope {
                remoteRepository.delete(scope = this, note = note)
            }
            // Refresh
            remoteRepository.fetch(scope = scope)
        }
        onDeleted(note.id)
    }

    override suspend fun clearLocalAppStorage() =
        syncManager.clearLocalStorage()

    override suspend fun isDataInSync(): Boolean =
        syncManager.isAllInSync()

    fun syncData(newScope: CoroutineScope? = null) =
        remoteRepository.sync(newScope ?: scope)

    // TODO: Not implemented
    override fun getAttachments(): Flow<Attachments> = emptyFlow()

    // TODO: Not implemented
    override suspend fun onPasswordChanged() {
    }

    // TODO: Not implemented
    override suspend fun canChangePassword(): Boolean = false

}
