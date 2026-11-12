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

        val result1 = AppServices
            .getAuthServiceByKey(FIREBASE_AUTH)
            .signOut()

        val result2 =
            (AppServices
                .getServiceByKey(GOOGLE_AUTH) as? AuthService)
                ?.signOut() ?: true

        val success = result1 && result2
        callback(success)
        if (success) {
            repo.clearAllData()
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

    open suspend fun onExport(
        destination: Any?,
        ctx: Any?,
    ) { }

    open suspend fun requestPermissions(
        context: Any?,
        onSuccess: (Any?) -> Unit,
        onUpdate: () -> Unit,
    ) { }

}