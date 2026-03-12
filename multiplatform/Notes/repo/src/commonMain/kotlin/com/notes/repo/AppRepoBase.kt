package com.notes.repo

import api.data.Attachments
import api.data.Notes
import api.repo.BaseRepo
import com.notes.db.ClientSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class AppRepoBase(
    private val syncManager: ClientSyncManager,
    private val remoteRepository: RemoteRepository = RemoteRepository(syncManager)
) : BaseRepo() {

    override fun getNotes(): Flow<List<Notes>> = flow {
        // Trigger sync with server
        syncData()
        // Trigger fetch from remote server
        remoteRepository.fetch(scope = scope)
        // Waiting on some data
        syncManager.notes.collect {
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
        onNewAdded: suspend (Long) -> Unit,
    ) {
        scope.launch {
            coroutineScope {
                remoteRepository.saveNote(scope = this, note = note)
            }
            // Refresh after data gets saved
            remoteRepository.fetch(scope = this)
        }
    }

    override fun deleteNote(
        note: Notes,
        onDeleted: (Long) -> Unit,
    ) {
        remoteRepository.delete(scope = scope, note = note)
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
