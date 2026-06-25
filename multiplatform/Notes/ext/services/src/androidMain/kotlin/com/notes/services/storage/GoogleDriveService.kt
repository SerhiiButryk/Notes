package com.notes.services.storage

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.activity.result.IntentSenderRequest
import api.AppService.Companion.GOOGLE_STORAGE
import api.Platform
import api.auth.AuthCallback
import api.data.AbstractStorageService
import api.data.Document
import api.data.toDocument
import api.data.toJson
import com.google.android.gms.auth.api.identity.AuthorizationClient
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.notes.repo.FilesManager
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * Service which implements Google Drive save and read capabilities.
 */
class GoogleDriveService : AbstractStorageService() {

    override val key = GOOGLE_STORAGE

    private var drive: Drive? = null

    // Location on the Google Drive. This folder is invisible for user.
    private val appDataFolder = "appDataFolder"
    private var client: AuthorizationClient? = null

    override var canUse: Boolean = false
        get() = drive != null

    private fun getDataFileName(document: Document): String {
        return if (document.isFile) document.file?.name ?: document.name
        else "${document.name}.json"
    }

    private fun isNoteDataFile(name: String): Boolean {
        return name.endsWith(".json")
    }

    private fun getMimetype(ext: String) =
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)

    // Saves to hidden folder on Google Drive's storage
    override suspend fun store(document: Document): Boolean {

        if (!canUse) return false

        val getContent: (Document, String) -> AbstractInputStreamContent = { document, mimetype ->
            if (document.isFile) {
                FileContent(mimetype, document.file)
            } else {
                val payload = document.toJson()
                ByteArrayContent.fromString("application/json", payload)
            }
        }

        val docName = getDataFileName(document)

        val oldFile = try {
            getFile(docName)
        } catch (e: Exception) {
            Platform().logger.logi("GoogleDriveService::store($docName) error = $e")
            return false
        }

        try {
            if (oldFile == null) {

                val file = File().apply {
                    name = docName
                    parents = listOf(appDataFolder)
                    if (document.isFile) {
                        val ext = document.file?.extension ?: throw IllegalStateException(
                            "GoogleDriveService::store() File extension can't be empty"
                        )
                        val mimetype = getMimetype(ext) ?: throw IllegalStateException(
                            "GoogleDriveService::store() Mime type is unknown"
                        )
                        mimeType = mimetype
                    }
                }

                Platform().logger.logi("GoogleDriveService::store($docName) creating a file...")

                val mediaContent = getContent(document, file.mimeType)

                drive!!
                    .files()
                    .create(file, mediaContent)
                    .execute()
            } else {

                if (!document.override) {
                    Platform().logger.logi(
                        "GoogleDriveService::store($docName) " +
                                "document is already saved, 'override' is 'false', so returning..."
                    )
                    return true
                }

                val file = File().apply {
                }

                Platform().logger.logi("GoogleDriveService::store($docName) updating...")

                val mediaContent = getContent(document, file.mimeType)

                drive!!
                    .files()
                    .update(oldFile.id, file, mediaContent)
                    .execute()
            }
        } catch (e: IOException) {
            Platform().logger.loge("GoogleDriveService::store($docName) not created or updated, error = '$e'")
            return false
        }

        Platform().logger.logi("GoogleDriveService::store($docName) done")
        return true
    }

    override suspend fun load(document: Document): Document? {

        if (!canUse) return null

        Platform().logger.logi("GoogleDriveService::load(${document.name})")

        val docName = getDataFileName(document)

        val file = try {
            getFile(docName) ?: return null
        } catch (e: Exception) {
            Platform().logger.logi("GoogleDriveService::load(${document.name}) error = $e")
            return null
        }

        val outputStream = ByteArrayOutputStream()

        var fileInput: InputStream? = null
        var fileOutput: FileOutputStream? = null

        try {
            if (document.isFile) {

                fileInput = drive!!.files().get(file.id).executeMediaAsInputStream()
                fileOutput = FileOutputStream(document.file)

                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (fileInput.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                Platform().logger.logi("GoogleDriveService::load($docName) done")

                return document
            } else {

                drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)

                Platform().logger.logi("GoogleDriveService::load($docName) done")

                val responseJson = outputStream.toString()

                return responseJson.toDocument()
            }
        } catch (e: IOException) {
            Platform().logger.loge("GoogleDriveService::load($docName) error = $e")
            return null
        } finally {
            fileInput?.close()
            fileOutput?.close()
        }
    }

    override suspend fun delete(document: Document): Boolean {
        Platform().logger.logi("GoogleDriveService::delete(${document.name})")

        if (!canUse) {
            Platform().logger.loge("GoogleDriveService::delete(${document.name}) service is not ready!")
            return false
        }

        return try {

            val docName = getDataFileName(document)

            val file = try {
                getFile(docName)
            } catch (e: Exception) {
                Platform().logger.logi("GoogleDriveService::store($docName) error = $e")
                return false
            }

            if (file != null) {
                try {
                    drive!!.files().delete(file.id).execute()
                    Platform().logger.logi("GoogleDriveService::delete(${document.name}) done")
                    true
                } catch (e: IOException) {
                    Platform().logger.loge("GoogleDriveService::delete(${document.name}) error = $e")
                    e.printStackTrace()
                    false
                }
            } else {
                // Return 'true' not to trigger an error state if the file doesn't exist
                Platform().logger.logi("GoogleDriveService::delete(${document.name}) no file to delete")
                true
            }
        } catch (e: Exception) {
            // Handle cases where file might already be gone
            Platform().logger.loge("GoogleDriveService::delete(${document.name}) error = $e")
            e.printStackTrace()
            false
        }
    }

    private fun getFile(name: String): File? {
        val files = drive!!.files()
            .list()
            .setSpaces(appDataFolder)
            .execute()
        return files.files.find { file ->
            file.name == name
        }
    }

    override suspend fun fetchAll(): List<Document> {

        if (!canUse) return emptyList()

        val list = mutableListOf<Document>()

        val fileList = try {
            drive!!.files()
                .list()
                .setSpaces(appDataFolder)
                .execute()
        } catch (e: IOException) {
            Platform().logger.logi("GoogleDriveService::fetchAll() error = $e")
            return emptyList()
        }

        for (file in fileList.files) {

            if (isNoteDataFile(file.name)) {

                // Handle note data files

                val outputStream = ByteArrayOutputStream()

                try {
                    drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                    Platform().logger.logi("GoogleDriveService::fetchAll() file = '${file.name}' downloaded")
                } catch (e: IOException) {
                    Platform().logger.loge("GoogleDriveService::fetchAll() error = '$e' file = '${file.name}'")
                    continue
                }

                val responseJson = outputStream.toString()
                list.add(responseJson.toDocument())
            } else {

                // Handle user files

                val fileManager = FilesManager()
                if (!fileManager.hasFile(file.name)) {
                    val outputStream = fileManager.getOutputStreamForImage(file.name)
                    try {
                        drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                        Platform().logger.loge("GoogleDriveService::fetchAll() file = '${file.name}' downloaded")
                    } catch (e: IOException) {
                        Platform().logger.loge("GoogleDriveService::fetchAll() error = '$e' file = '${file.name}'")
                        continue
                    }
                } else {
                    Platform().logger.logi("GoogleDriveService::fetchAll() file = '${file.name}' is present!")
                }
            }

        }

        Platform().logger.logi("GoogleDriveService::fetchAll() size = '${list.size}'")

        return list
    }

    /**
     * Ask user to grant permission of reading and writing for our Google Drive Service
     * using user selected account
     */
    suspend fun askForAccess(activityContext: Any?, callback: AuthCallback?) {

        Platform().logger.logi("GoogleDriveService::askForAccess()")

        val requestedScopes = listOf(Scope(DriveScopes.DRIVE_APPDATA))

        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)
            .build()

        val token = suspendCancellableCoroutine { continuation ->
            client = Identity.getAuthorizationClient(activityContext as Context)
            client!!.authorize(authorizationRequest)
                .addOnSuccessListener { authorizationResult ->
                    if (authorizationResult.hasResolution()) {
                        Platform().logger.logi("GoogleDriveService::askForAccess() Success.")
                        // The user needs to grant permission via a popup
                        val pendingIntent = authorizationResult.pendingIntent
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(pendingIntent!!).build()
                        callback?.onUserAction(intentSenderRequest)
                    } else {
                        // Permission already granted
                        Platform().logger.logi("GoogleDriveService::askForAccess() Drive access already authorized.")
                    }
                    continuation.resume(authorizationResult.accessToken) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                }
                .addOnFailureListener { e ->
                    Platform().logger.loge("GoogleDriveService::askForAccess() Authorization failed: ${e.message}")
                    continuation.resume(null) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                }
        }

        if (!token.isNullOrEmpty() && drive == null) {
            initGoogleDrive(token)
        }

        Platform().logger.logi("GoogleDriveService::askForAccess() ready = $canUse")

        // Notify about update
        callback?.onUserAction(null)
    }

    private fun initGoogleDrive(token: String) {

        val accessToken = AccessToken(token, null)

        val builder = GoogleCredentials.newBuilder().setAccessToken(accessToken)

        val credentials = object : GoogleCredentials(builder) {
            // WE DO not support the access token refresh.
            // Ideally it should be done on backend, but we don't have it
            // so can't do this safely. User should log in to get new access
            override fun refreshAccessToken(): AccessToken {
                return AccessToken("", null)
            }
        }

        credentials.createScoped(listOf(DriveScopes.DRIVE_APPDATA))

        drive = Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(credentials)
        ).setApplicationName("Notes").build()
    }

}