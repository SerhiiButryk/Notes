package com.notes.repo.feature

import api.Platform
import api.data.isEqualTo
import com.notes.repo.AppRepository
import com.notes.repo.FilesManager
import com.notes.repo.RemoteRepository
import kotlinx.coroutines.coroutineScope
import java.io.File

internal class ChangePasswordUseCase(
    rootFileDir: File,
) {

    private val filesManager = FilesManager(rootFileDir)

    suspend fun canChangePassword(repo: AppRepository, remoteRepo: RemoteRepository): Boolean {

        if (!Platform().netStateManager.isNetworkAvailable()) {
            Platform().logger.loge("PasswordHelper::canChangePassword() no network")
            return false
        }

        if (!repo.isDataInSync()) {
            Platform().logger.loge("PasswordHelper::canChangePassword() " +
                    "local data is not up-to-date with remote")
            return false
        }

        val remoteNotes = remoteRepo.fetchCopy()
        if (!remoteNotes.isEqualTo(repo.cachedLocalNotes)) {
            Platform().logger.loge("PasswordHelper::canChangePassword() " +
                    "remote data is not up-to-date with local data")
            return false
        }

        // Create backup files
        val result = filesManager.saveToDisk(remoteNotes)
        Platform().logger.logi("PasswordHelper::canChangePassword: $result")
        return result
    }

    suspend fun onPasswordChanged(repo: AppRepository, remoteRepo: RemoteRepository) {
        Platform().logger.logi("PasswordHelper::onPasswordChanged()")

        coroutineScope {
            val notesFromDisk = filesManager.readFromDisk()
            for (note in notesFromDisk) {
                remoteRepo.saveNote(scope = this, note = note)
            }
        }

        repo.clearLocalAppStorage()

        coroutineScope {
            // This should get our app in sync when password has changed
            remoteRepo.fetch(scope = this)
        }

        Platform().logger.logi("PasswordHelper::onPasswordChanged() done")
    }

}