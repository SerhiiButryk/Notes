package com.notes.notes_ui

import android.content.Context
import android.net.Uri
import androidx.activity.result.IntentSenderRequest
import api.AppService.Companion.FIREBASE_AUTH
import api.AppService.Companion.GOOGLE_AUTH
import api.AppService.Companion.GOOGLE_STORAGE
import api.AppServices
import api.auth.AuthCallback
import api.repo.Repository
import com.notes.notes_ui.data.AccountInfo
import com.notes.notes_ui.features.PdfConverter
import com.notes.notes_ui.features.toHtml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SettingsInteractor(private val repo: Repository) {

    suspend fun onExport(uri: Uri, context: Context) {

        val notesHtml = repo
            .getNotes()
            .first()
            .toHtml()

        withContext(Dispatchers.Main) {
            val converted = PdfConverter()
            converted.convertHtmlToPdf(context, notesHtml, "notes.pdf", uri)
        }
    }

    suspend fun singOut(callback: (Boolean) -> Unit) {

        AppServices
            .getAuthServiceByKey(FIREBASE_AUTH)
            .signOut()

        val result = AppServices
            .getAuthServiceByKey(GOOGLE_AUTH)
            .signOut()

        callback(result)
        if (result) {
            repo.clearLocalAppStorage()
        }

    }

    suspend fun getAccountInfo(pending: Boolean = false): AccountInfo {

        val email = AppServices
            .getDefaultAuthService()
            .getUserEmail()

        val googleIsActive = AppServices
            .getAuthServiceByKey(GOOGLE_AUTH)
            .isAuthenticated()

        val firebaseIsActive = AppServices
            .getAuthServiceByKey(FIREBASE_AUTH)
            .isAuthenticated()

        val googleDriveIsActive = AppServices
            .getStoreServicesByKey(GOOGLE_STORAGE)
            .canUse

        val grantPermission = !googleDriveIsActive

        return AccountInfo(
            email = email,
            googleIsActive = googleIsActive,
            firebaseIsActive = firebaseIsActive,
            googleDriveIsActive = googleDriveIsActive,
            showGrantPermissions = grantPermission,
            pending = pending,
            syncCompleted = repo.isDataInSync()
        )
    }

    suspend fun requestPermissions(
        context: Any?,
        onSuccess: (IntentSenderRequest) -> Unit,
        onUpdate: () -> Unit
    ) {
        val service = AppServices.getAuthServiceByKey(GOOGLE_AUTH)
        service.resetSettings()
        val callback = object : AuthCallback {
            override fun onUserAction(data: Any?) {
                if (data != null) {
                    onSuccess(data as IntentSenderRequest)
                }
                service.setAuthCallback(null)
                // Update status
                onUpdate()
            }
        }
        service.setAuthCallback(callback)
        // Will sign in and ask permission from user
        service.login(activityContext = context, pass = "", email = "")
        // Update status
        onUpdate()
    }

}