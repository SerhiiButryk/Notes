package com.notes.notes_ui

import android.content.Context
import android.net.Uri
import androidx.activity.result.IntentSenderRequest
import api.AppServices
import api.auth.AuthCallback
import com.notes.data.LocalNoteDatabase
import com.notes.data.isAllInSyncWithRemote
import com.notes.notes_ui.data.AccountInfo
import com.notes.notes_ui.data.AppRepository
import com.notes.notes_ui.data.RemoteRepository
import com.notes.notes_ui.features.PdfConverter
import com.notes.notes_ui.features.toHtml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SettingsInteractor {

    suspend fun onExport(uri: Uri, context: Context) {

        val repo = AppRepository(RemoteRepository(AppServices.dataStoreService))

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
        val result = AppServices
            .getAuthServiceByName("google")!!
            .signOut()
        callback(result)
        if (result) {
            // Will be clearing database completely
            LocalNoteDatabase.access().delete()
        }
    }

    suspend fun getAccountInfo(pending: Boolean = false): AccountInfo {

        val email = AppServices
            .getDefaultAuthService()!!
            .getUserEmail()

        val googleIsActive = AppServices
            .getAuthServiceByName("google")!!
            .isAuthenticated()

        val firebaseIsActive = AppServices
            .getAuthServiceByName("firebase")!!
            .isAuthenticated()

        val googleDriveIsActive = AppServices
            .getStoreService("googledrive")!!
            .canUse

        val grantPermission = !googleDriveIsActive

        val syncCompleted = isAllInSyncWithRemote()

        return AccountInfo(
            email = email,
            googleIsActive = googleIsActive,
            firebaseIsActive = firebaseIsActive,
            googleDriveIsActive = googleDriveIsActive,
            showGrantPermissions = grantPermission,
            pending = pending,
            syncCompleted = syncCompleted
        )
    }

    suspend fun requestPermissions(
        context: Any?,
        onSuccess: (IntentSenderRequest) -> Unit,
        onUpdate: () -> Unit
    ) {
        val service = AppServices
            .getAuthServiceByName("google")!!
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