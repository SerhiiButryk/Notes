package com.notes.repo

import android.content.Context
import api.AppServices
import api.Platform
import api.data.AbstractStorageService
import api.data.Image
import api.data.Notes
import api.repo.BaseRepo
import com.notes.db.LocalNoteDatabase
import com.notes.db.isAllInSyncWithRemote
import com.notes.db.json.isPendingDeletionOnRemote
import com.notes.db.toEntity
import com.notes.db.toNote
import com.notes.repo.feature.ChangePasswordUseCase
import com.notes.repo.feature.MediaStoreUseCase
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
import java.io.File

class AppRepository private constructor(
    private val remoteRepository: RemoteRepository,
    filesDir: File,
) : BaseRepo(filesDir) {

    companion object {

        // Factory to create this repository

        fun create(context: Context): AppRepository {
            return AppRepository(
                remoteRepository = RemoteRepository(AppServices.dataStoreService),
                filesDir = context.filesDir,
            )
        }

        fun create(context: Context, services: List<AbstractStorageService>): AppRepository {
            return AppRepository(
                remoteRepository = RemoteRepository(services),
                filesDir = context.filesDir,
            )
        }
    }

    private val coroutineContext = SupervisorJob() + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    var cachedLocalNotes: List<Notes> = emptyList()

    private val changePass = ChangePasswordUseCase(filesDir)
    private val mediaStore = MediaStoreUseCase(scope, filesDir)

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
            val note = if (newNote) {
                val id = db.insertNote(note.toEntity(setId = false /* Use auto-increment */))
                onNewAdded(id)
                Platform().logger.logi("OfflineRepository::saveNote($id) new record is added locally")
                note.copy(id = id)
            } else {
                db.updateNote(note.toEntity())
                Platform().logger.logi("OfflineRepository::saveNote(${note.id}) is updated locally")
                note
            }
            remoteRepository.saveNote(scope, note)
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
            // Delete local files related of this note
            mediaStore.onDelete(note.id)
            // Notify UI
            callback(note.id)
        }
    }

    override suspend fun canChangePassword(): Boolean {
        return changePass.canChangePassword(this, remoteRepository)
    }

    override suspend fun onPasswordChanged() {
        return changePass.onPasswordChanged(this, remoteRepository)
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

    override fun onAttachments(attachment: Any, noteId: Long, info: Any?) {
        mediaStore.onAttachments(attachment, noteId, info)
    }

    override fun getAttachments(filesDir: File) = mediaStore.getAttachments(filesDir)

    override fun onDelete(image: Image) {
        mediaStore.onDelete(image)
    }
}
