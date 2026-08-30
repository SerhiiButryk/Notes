package com.notes.notes_ui

import android.content.Context
import android.net.Uri
import androidx.activity.result.IntentSenderRequest
import api.AppService.Companion.GOOGLE_AUTH
import api.AppServices
import api.auth.AuthCallback
import api.repo.Repository
import com.notes.notes_ui.features.PdfConverter
import com.notes.notes_ui.features.toHtml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SettingsInteractor(
    repo: Repository
) : SettingsInteractorBase(repo) {

    suspend fun onExport(
        uri: Uri,
        context: Context,
    ) {
        val notesHtml =
            repo
                .getNotes()
                .first()
                .toHtml()

        withContext(Dispatchers.Main) {
            val converted = PdfConverter()
            converted.convertHtmlToPdf(context, notesHtml, "notes.pdf", uri)
        }
    }

    suspend fun requestPermissions(
        context: Any?,
        onSuccess: (IntentSenderRequest) -> Unit,
        onUpdate: () -> Unit,
    ) {
        val service = AppServices.getAuthServiceByKey(GOOGLE_AUTH)
        service.setAccountAutoselect(false)
        val callback =
            object : AuthCallback {
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
