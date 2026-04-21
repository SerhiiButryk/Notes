package com.notes.repo

import api.AppServices
import api.Platform
import api.data.Notes
import api.repo.Repository
import com.notes.db.LocalNoteDatabase
import com.notes.db.isAllInSyncWithRemote
import com.notes.db.json.isPendingDeletionOnRemote
import com.notes.db.toEntity
import com.notes.db.toNote
import com.notes.db.updateMetadataForNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
    }

    private val coroutineContext = SupervisorJob() + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    private var cachedLocalNotes: List<Notes> = emptyList()

    override fun getNotes(): Flow<List<Notes>> =
        flow {
            Platform().logger.logi("OfflineRepository::getNotes()")

            // TODO: Might do this periodically
            // in Work manager or something else
            // Trigger fetch from remote server
            remoteRepository.fetchNotes(scope)

            val db = LocalNoteDatabase.accessNoteMetadata()

            // Trigger load from local db
            // 1. Load note mata data (additional info)
            // 2. Check it and load a note which is referenced by 'original'

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
            Platform().logger.logi("OfflineRepository::deleteNote(${note.id})")

            // 1. Update metadata
            // 2. Delete note on remote. Will update metadata on success
            // 3. Then can delete note locally

            // Remember that we are going to delete current notes
            updateMetadataForNote(note = note, pendingDelete = true)

            // Notify UI
            callback(note.id)

            // Trigger remote deletion
            remoteRepository.delete(scope, note)
        }
    }

    override suspend fun triggerSyncWithRemote() {
        val job = scope.launch {
            Platform().logger.logi("OfflineRepository::triggerFullDataSync() started")

            // Update remote datastore using new key

            //TODO 'cachedLocalNotes' must be the source of truth
            for (note in cachedLocalNotes) {
                remoteRepository.saveNote(this, note)
            }

            Platform().logger.logi("OfflineRepository::triggerFullDataSync() finished")
        }
        // Wait completion
        job.join()
    }

    override suspend fun clearLocalNotesStorage() {
        // Will be clearing database completely
        LocalNoteDatabase.access().delete()
    }

    override suspend fun isDataInSync() = isAllInSyncWithRemote()

    override fun clear() {
        Platform().logger.logi("OfflineRepository::clear()")
        scope.cancel()
    }

}
