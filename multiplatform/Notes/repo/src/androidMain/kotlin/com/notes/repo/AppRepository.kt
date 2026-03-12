package com.notes.repo

import api.Platform
import api.data.AbstractStorageService
import api.data.Notes
import api.data.UserFile
import api.repo.BaseRepo
import com.notes.db.AndroidSyncManager
import com.notes.db.LocalNoteDatabase
import com.notes.db.isAllInSyncWithRemote
import com.notes.db.impl.isPendingDeletionOnRemote
import com.notes.db.toEntity
import com.notes.db.toNote
import com.notes.repo.feature.ChangePasswordUseCase
import com.notes.repo.feature.MediaStoreUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AppRepository private constructor(
    private val remoteRepository: RemoteRepository,
) : BaseRepo() {
    companion object {
        // Factory to create this repository

        fun create(): AppRepository =
            AppRepository(
                remoteRepository = RemoteRepository(syncManager = AndroidSyncManager()),
            )

        fun create(services: List<AbstractStorageService>): AppRepository =
            AppRepository(
                remoteRepository = RemoteRepository(services, AndroidSyncManager()),
            )
    }

    var cachedLocalNotes: List<Notes> = emptyList()

    private val changePass = ChangePasswordUseCase()
    private val mediaStore = MediaStoreUseCase(scope)

    override fun getNotes(): Flow<List<Notes>> =
        flow {
            Platform().logger.logi("AppRepository::getNotes()")

            // TODO: Log internal folder content
            // FilesManager().printFolderInfo()

            // TODO: We may do this periodically on demand

            // Trigger sync with server
            syncData()

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

                    if (!metadata.metadata.isPendingDeletionOnRemote() && !metadata.pendingDelete) {
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
            Platform().logger.logi("AppRepository::getNotes(id=$id)")

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
            val note =
                if (newNote) {
                    val id = db.insertNote(note.toEntity(setId = false /* Use auto-increment */))
                    onNewAdded(id)
                    Platform().logger.logi("AppRepository::saveNote($id) new record is added locally")
                    note.copy(id = id)
                } else {
                    db.updateNote(note.toEntity())
                    Platform().logger.logi("AppRepository::saveNote(${note.id}) is updated locally")
                    note
                }
            remoteRepository.saveNote(scope, note)
        }
    }

    override fun deleteNote(
        note: Notes,
        onDeleted: (Long) -> Unit,
    ) = deleteNote(scope = scope, note = note, callback = onDeleted)

    fun deleteNote(
        scope: CoroutineScope,
        note: Notes,
        callback: (Long) -> Unit,
    ) {
        scope.launch {
            Platform().logger.logi("AppRepository::delete(${note.id})")
            // Trigger deletion
            remoteRepository.delete(scope, note)
            // Delete local files related of this note
            mediaStore.onDelete(note.id)
            // Notify UI
            callback(note.id)
        }
    }

    override suspend fun canChangePassword(): Boolean = changePass.canChangePassword(this, remoteRepository)

    override suspend fun onPasswordChanged() = changePass.onPasswordChanged(this, remoteRepository)

    override suspend fun clearLocalAppStorage() {
        // Will be clearing database completely
        LocalNoteDatabase.access().delete()
    }

    override suspend fun isDataInSync() = isAllInSyncWithRemote()

    fun syncData(newScope: CoroutineScope? = null) = remoteRepository.sync(newScope ?: scope)

    override suspend fun onAttachments(
        attachment: Any,
        noteId: Long,
        info: Any?,
    ): Boolean =
        scope
            .async {
                val createdFile = mediaStore.onAttachments(attachment, noteId, info)
                if (createdFile != null) {
                    val result = remoteRepository.saveAttachment(file = createdFile)
                    if (!result) {
                        // Delete created file
                        createdFile.delete()
                    }
                    result
                } else {
                    false
                }
            }.await()

    override fun getAttachments() = mediaStore.getAttachments()

    override suspend fun onDeleteAttachment(file: UserFile): Boolean {
        // First, delete on the remote
        return if (remoteRepository.deleteAttachment(scope = scope, name = file.file.name)) {
            // Then delete locally
            mediaStore.onDelete(file)
            true
        } else {
            false
        }
    }
}
