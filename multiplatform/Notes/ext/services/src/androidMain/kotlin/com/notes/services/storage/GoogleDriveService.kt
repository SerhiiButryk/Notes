package com.notes.services.storage

import android.content.Context
import androidx.activity.result.IntentSenderRequest
import api.PlatformAPIs.logger
import api.auth.AuthCallback
import api.data.AbstractStorageService
import api.data.Document
import api.data.toDocument
import api.data.toJson
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
import kotlin.collections.find


/**
 * Service which implements Google Drive save and read capabilities.
 */
class GoogleDriveService : AbstractStorageService() {

    override val name: String = "googledrive"

    private var drive: Drive? = null

    // Location on the Google Drive. This folder is invisible for user.
    private val appDataFolder = "appDataFolder"

    override var canUse: Boolean = false
        get() = drive != null

    private fun getDataFileName(name: String) = "$name.json"

    override suspend fun store(document: Document): Boolean {

        if (!canUse) return false

        val fileMetadata = File().apply {
            name = getDataFileName(document.name)
            parents = listOf(appDataFolder) // Saves to hidden folder on Google Drive's storage
        }

        val payload = document.toJson()

        val content = ByteArrayContent.fromString("application/json", payload)

        drive!!
            .files()
            .create(fileMetadata, content)
            .execute()

        logger.logi("GoogleDriveService::store() done")

        return true
    }

    override suspend fun load(name: String): Document? {

        if (!canUse) return null

        val file = getFile(name) ?: return null

        val outputStream = ByteArrayOutputStream()
        drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)

        val responseJson = outputStream.toString()

        logger.logi("GoogleDriveService::load() done")

        return responseJson.toDocument()
    }

    override suspend fun delete(name: String): Boolean {
        if (!canUse) return false
        try {
            val file = getFile(name)
            if (file != null) {
                drive!!.files().delete(file.id).execute()
                logger.logi("GoogleDriveService::delete() done")
                return true
            }
        } catch (e: Exception) {
            // Handle cases where file might already be gone
            logger.loge("GoogleDriveService::delete() error = $e")
            e.printStackTrace()
        }
        return false
    }

    private fun getFile(name: String): File? {
        val fileList = drive!!.files()
            .list()
            .setSpaces(appDataFolder)
            .execute()
        return fileList.files.find { file ->
            file.name == getDataFileName(name)
        }
    }

    override suspend fun fetchAll(): List<Document> {

         if (!canUse) return emptyList()

        val list = mutableListOf<Document>()

        val fileList = drive!!.files()
            .list()
            .setSpaces(appDataFolder)
            .execute()

        for (file in fileList.files) {

            val outputStream = ByteArrayOutputStream()
            drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)

            val responseJson = outputStream.toString()
            list.add(responseJson.toDocument())
        }

        logger.logi("GoogleDriveService::fetchAll() size = '${list.size}'")

        return list
    }

    /**
     * Ask user to grant permission of reading and writing for our Google Drive Service
     * using user selected account
     */
    suspend fun askForAccess(activityContext: Any?, callback: AuthCallback?) {

        logger.logi("GoogleDriveService::askForAccess()")

        val requestedScopes = listOf(Scope(DriveScopes.DRIVE_APPDATA))

        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)
            .build()

        val token = suspendCancellableCoroutine { continuation ->
            Identity.getAuthorizationClient(activityContext as Context)
                .authorize(authorizationRequest)
                .addOnSuccessListener { authorizationResult ->
                    if (authorizationResult.hasResolution()) {
                        logger.logi("GoogleDriveService::askForAccess() Success.")
                        // The user needs to grant permission via a popup
                        val pendingIntent = authorizationResult.pendingIntent
                        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent!!).build()
                        callback?.onUserAction(intentSenderRequest)
                    } else {
                        // Permission already granted
                        logger.logi("GoogleDriveService::askForAccess() Drive access already authorized.")
                    }
                    continuation.resume(authorizationResult.accessToken) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                }
                .addOnFailureListener { e ->
                    logger.loge("GoogleDriveService::askForAccess() Authorization failed: ${e.message}")
                    continuation.resume("") { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                }
        }

        if (!token.isNullOrEmpty() && drive == null) {
            drive = getDriveService(token)
        }

        logger.logi("GoogleDriveService::askForAccess() ready = $canUse")

        // Notify about update
        callback?.onUserAction(null)
    }

    private fun getDriveService(accessToken: String): Drive {

        val accessToken = AccessToken(accessToken, null)

        val credentials = GoogleCredentials.create(accessToken)
            .createScoped(listOf(DriveScopes.DRIVE_APPDATA))

        return Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(credentials)
        ).setApplicationName("Notes").build()
    }

}