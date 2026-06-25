package com.notes.repo.feature

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileObserver
import android.provider.OpenableColumns
import api.Platform
import api.data.Attachments
import api.data.Document.Companion.createFileName
import api.data.UserFile
import com.notes.repo.FilesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File

class MediaStoreUseCase(
    private val scope: CoroutineScope,
) {

    private val filesManager = FilesManager()

    fun onAttachments(
        attachment: Any, noteId: Long, info: Any?
    ): File? {
        val context = info as Context
        val uri = attachment as Uri
        val name = getImageNameFromUri(context, uri) ?: throw IllegalStateException("Can't get file name")
        val fileName = createFileName(noteId, name)
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                return filesManager.saveImage(
                    inputStream = inputStream,
                    fileName = fileName
                )
            }
        } catch (e: Exception) {
            Platform().logger.logi("MediaStoreUseCase::onAttachments: failed, error = $e")
        }
        return null
    }

    fun getAttachments(): Flow<Attachments> =
        filesManager
            .getOrCreateImageFolder()
            .observeAsFlow()

    fun onDelete(file: UserFile) {
        filesManager.delete(file)
    }

    fun onDelete(noteId: Long) {
        filesManager.deleteAllFor(noteId)
    }

    private fun getImageNameFromUri(context: Context, uri: Uri): String? {

        var fileName: String? = null

        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                // Move to the first row of the cursor results
                if (cursor.moveToFirst()) {
                    // Find the index of the Display Name column
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
        }

        // Fallback for File URIs or if the ContentResolver query returned null
        if (fileName == null) {
            fileName = uri.lastPathSegment
        }

        return fileName
    }

    private fun File.observeAsFlow(): Flow<Attachments> = callbackFlow {

        val mask: Int = FileObserver.CREATE or FileObserver.DELETE or
                FileObserver.MODIFY or FileObserver.MOVED_TO

        val observer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            object : FileObserver(this@observeAsFlow, mask) {
                override fun onEvent(event: Int, path: String?) {
                    val attachments = filesManager.scanFolder(this@observeAsFlow.path)
                    trySend(attachments)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            object : FileObserver(this@observeAsFlow.absolutePath, mask) {
                override fun onEvent(event: Int, path: String?) {
                    val attachments = filesManager.scanFolder(this@observeAsFlow.path)
                    trySend(attachments)
                }
            }
        }

        // Start watching the file system folder
        observer.startWatching()

        val attachments = filesManager.scanFolder(path)
        trySend(attachments)

        // Keep the Flow active. When the collector cancels or its lifecycle scope ends,
        // this block executes to clean up resources and prevent memory leaks.
        awaitClose {
            observer.stopWatching()
        }
    }

}