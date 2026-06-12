package com.notes.services.storage

import android.content.Context
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
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.io.IOException


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

    private fun getDataFileName(name: String) = "$name.json"

    override suspend fun store(document: Document): Boolean {

        if (!canUse) return false

        val file = File().apply {
            name = getDataFileName(document.name)
            parents = listOf(appDataFolder) // Saves to hidden folder on Google Drive's storage
        }

        val payload = document.toJson()

        val content = ByteArrayContent.fromString("application/json", payload)

        val oldFile = getFile(file.name)

        try {
            if (oldFile == null) {
                Platform().logger.logi("GoogleDriveService::store() creating... file = '${file.name}'")
                drive!!
                    .files()
                    .create(file, content)
                    .execute()
            } else {
                Platform().logger.logi("GoogleDriveService::store() updating... file = '${file.name}'")
                drive!!
                    .files()
                    .update(oldFile.id, file, content)
                    .execute()
            }
        } catch (e: IOException) {
            Platform().logger.loge("GoogleDriveService::store() not created or updated, error = '$e'")
            return false
        }

        Platform().logger.logi("GoogleDriveService::store() done")
        return true
    }

    override suspend fun load(name: String): Document? {

        if (!canUse) return null

        val file = getFile(name) ?: return null

        val outputStream = ByteArrayOutputStream()

        try {
            drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)
        } catch (e: IOException) {
            Platform().logger.loge("GoogleDriveService::load() error = $e")
            return null
        }

        val responseJson = outputStream.toString()

        Platform().logger.logi("GoogleDriveService::load() done")

        return responseJson.toDocument()
    }

    override suspend fun delete(name: String): Boolean {
        if (!canUse) return false
        try {
            val file = getFile(name)
            if (file != null) {
                try {
                    drive!!.files().delete(file.id).execute()
                } catch (e: IOException) {
                    Platform().logger.loge("GoogleDriveService::delete() error = $e")
                    return false
                }
                Platform().logger.logi("GoogleDriveService::delete() done")
                return true
            }
        } catch (e: Exception) {
            // Handle cases where file might already be gone
            Platform().logger.loge("GoogleDriveService::delete() error = $e")
            e.printStackTrace()
        }
        return false
    }

    private fun getFile(name: String): File? {
        val fileList = try {
            drive!!.files()
                .list()
                .setSpaces(appDataFolder)
                .execute()
        } catch (e: IOException) {
            Platform().logger.loge("GoogleDriveService::getFile() error = $e")
            return null
        }
        return fileList.files.find { file ->
            file.name == getDataFileName(name)
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
            Platform().logger.loge("GoogleDriveService::fetchAll() error = $e")
            return emptyList()
        }

        for (file in fileList.files) {

            val outputStream = ByteArrayOutputStream()

            try {
                drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)
            } catch (e: IOException) {
                Platform().logger.loge("GoogleDriveService::fetchAll() error = '$e' file = '${file.id}'")
                continue
            }

            val responseJson = outputStream.toString()
            list.add(responseJson.toDocument())
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