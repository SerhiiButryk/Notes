package com.notes.repo

import api.Platform
import api.data.AbstractStorageService
import api.data.UserFile
import com.notes.repo.feature.ChangePasswordUseCase
import com.notes.repo.feature.MediaStoreUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class AppRepository private constructor(
    private val remoteRepository: RemoteRepository,
    // For test support
    scopeOverride: CoroutineScope? = null,
) : AppRepoBase(remoteRepository.syncManager, remoteRepository, scopeOverride) {

    companion object {
        // Factory to create this repository

        fun create(syncManager: AndroidSyncManager): AppRepository =
            AppRepository(
                remoteRepository = RemoteRepository(syncManager),
            )

        fun create(
            services: List<AbstractStorageService>,
            syncManager: AndroidSyncManager,
            scope: CoroutineScope? = null,
        ): AppRepository =
            AppRepository(
                scopeOverride = scope,
                remoteRepository =
                    RemoteRepository(
                        services,
                        syncManager
                    ),
            )
    }

    init {
        // Set scope
        remoteRepository.syncManager.scope = scope
        Platform().logger.logi("AppRepository() created")
    }

    private val changePass = ChangePasswordUseCase()
    private val mediaStore = MediaStoreUseCase(scope)

    override suspend fun canChangePassword(): Boolean =
        changePass.canChangePassword(this, remoteRepository)

    override suspend fun onPasswordChanged() =
        changePass.onPasswordChanged(this, remoteRepository)

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
