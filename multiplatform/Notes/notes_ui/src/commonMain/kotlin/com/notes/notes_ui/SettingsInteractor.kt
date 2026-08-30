package com.notes.notes_ui

import api.AppService.Companion.FIREBASE_AUTH
import api.AppService.Companion.GOOGLE_AUTH
import api.AppService.Companion.GOOGLE_STORAGE
import api.AppServices
import api.auth.AuthService
import api.data.AbstractStorageService
import api.repo.Repository
import com.notes.notes_ui.models.AccountInfoState

open class SettingsInteractorBase(
    protected val repo: Repository,
) {

    suspend fun singOut(callback: (Boolean) -> Unit) {
        AppServices
            .getAuthServiceByKey(FIREBASE_AUTH)
            .signOut()

        val result =
            (AppServices
                .getServiceByKey(GOOGLE_AUTH) as? AuthService)
                ?.signOut() ?: true

        callback(result)
        if (result) {
            repo.clearLocalAppStorage()
        }
    }

    suspend fun getAccountInfo(pending: Boolean = false): AccountInfoState {
        val email =
            AppServices
                .getDefaultAuthService()
                .getUserEmail()

        val googleIsActive =
            (AppServices
                .getServiceByKey(GOOGLE_AUTH) as? AuthService)
                ?.isAuthenticated() ?: false

        val firebaseIsActive =
            AppServices
                .getAuthServiceByKey(FIREBASE_AUTH)
                .isAuthenticated()

        val googleDriveIsActive =
            (AppServices
                .getServiceByKey(GOOGLE_STORAGE) as? AbstractStorageService)
                ?.canUse ?: false

        val grantPermission = !googleDriveIsActive

        return AccountInfoState(
            email = email,
            googleIsActive = googleIsActive,
            firebaseIsActive = firebaseIsActive,
            googleDriveIsActive = googleDriveIsActive,
            showGrantPermissions = grantPermission,
            pending = pending,
            syncCompleted = repo.isDataInSync(),
        )
    }

}