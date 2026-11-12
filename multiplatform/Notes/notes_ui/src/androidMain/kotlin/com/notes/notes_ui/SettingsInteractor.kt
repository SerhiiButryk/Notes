package com.notes.notes_ui

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import androidx.activity.result.IntentSenderRequest
import api.AppService.Companion.GOOGLE_AUTH
import api.AppServices
import api.Platform
import api.auth.AuthCallback
import api.repo.Repository
import com.notes.notes_ui.features.PdfConverter
import com.notes.notes_ui.features.toHtml
import com.notes.repo.FilesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class SettingsInteractor(
    repo: Repository
) : SettingsInteractorBase(repo) {

    override suspend fun onExport(
        destination: Any?,
        ctx: Any?,
    ) {

        val context = ctx as Context
        val destinationUri = destination as Uri

        // Export notes

        val notesHtml =
            repo
                .getNotes()
                .first()
                .toHtml()

        withContext(Dispatchers.Main) {
            val converter = PdfConverter()
            converter.convertHtmlToPdf(
                context = context,
                htmlString = notesHtml,
                fileName = "notes.pdf",
                folderUri = destinationUri)
        }

        // Export media

        val fileManager = FilesManager()
        val images = fileManager.getOrCreateImageFolder()
        val attachments = fileManager.scanFolder(images.path)

        for (userFile in attachments.files) {
            copyFileToUri(
                context = context,
                sourceFile = userFile.file,
                destinationUri = destinationUri
            )
        }
    }

    private fun copyFileToUri(context: Context, sourceFile: File, destinationUri: Uri) {
        try {
            val extension = sourceFile.extension.lowercase()
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            require(mimeType != null) { "Mime type is null" }
            val uri = createNewFileInTree(
                context = context,
                treeUri = destinationUri,
                mimeType = mimeType,
                displayName = sourceFile.name,
            )
            context.contentResolver.openOutputStream(uri!!)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Platform().logger.logd("copyFileToUri: '$sourceFile' done")
        } catch (e: Exception) {
            Platform().logger.loge("copyFileToUri: error: '$e'")
            e.printStackTrace()
        }
    }

    fun createNewFileInTree(context: Context, treeUri: Uri, mimeType: String, displayName: String): Uri? {
        val docId = DocumentsContract.getTreeDocumentId(treeUri)
        val parentDocumentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)
        return DocumentsContract.createDocument(
            context.contentResolver,
            parentDocumentUri,
            mimeType,
            displayName
        )
    }

    override suspend fun requestPermissions(
        context: Any?,
        onSuccess: (Any?) -> Unit,
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
