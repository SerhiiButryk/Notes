package com.notes.services.storage

import android.content.Context
import androidx.activity.result.IntentSenderRequest
import api.AppServices
import api.PlatformAPIs.logger
import api.auth.AuthCallback
import api.data.AbstractStorageService
import api.data.Document
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream


/**
 * Service which implements Google Drive save and read capabilities.
 */
class GoogleDriveService : AbstractStorageService() {

    override val name: String = "googledrive"

    private var drive: Drive? = null

    // Invisible to the user, we use it only to as storage for app data
    private val appDataFolder = "appDataFolder"

    override suspend fun store(document: Document): Boolean {

        if (drive == null) return false

        val data = """{"name":"${document.name}","content":"${document.data}"}"""

        val fileMetadata = File().apply {
            name = "${document.name}.json"
            parents = listOf(appDataFolder) // Saves to hidden folder
        }

        val content = ByteArrayContent.fromString("application/json", data)
        drive!!.files().create(fileMetadata, content).execute()

        logger.logi("GoogleDriveService::store() done")

        return true
    }

    override suspend fun load(name: String): Document? {

        if (drive == null) return null

        val result = drive!!.files().list()
            .setSpaces(appDataFolder)
            .execute()

        val file = result.files.find { file ->
            file.name == "$name.json"
        } ?: return null

        val outputStream = ByteArrayOutputStream()
        drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)

        val content = outputStream.toString()

        logger.logi("GoogleDriveService::load() loaded: $content")

        return Document("", "")
    }

    override suspend fun delete(name: String): Boolean {
        if (drive == null) return false
        try {
            drive!!.files().delete("$name.json").execute()
            logger.logi("GoogleDriveService::delete() done")
            return true
        } catch (e: Exception) {
            // Handle cases where file might already be gone
            e.printStackTrace()
        }
        return false
    }

    override suspend fun fetchAll(): List<Document> {
        return emptyList()
    }

    suspend fun askForAccess(activityContext: Any?, callback: AuthCallback?) {

        logger.logi("GoogleDriveService::askForAccess()")

        val requestedScopes = listOf(Scope(DriveScopes.DRIVE_APPDATA))

        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)
            .build()

        val result = suspendCancellableCoroutine { continuation ->
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
                    continuation.resume(true) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                }
                .addOnFailureListener { e ->
                    logger.loge("GoogleDriveService::askForAccess() Authorization failed: ${e.message}")
                    continuation.resume(false) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                }
        }

        if (result && drive == null) {
            val email = AppServices.getDefaultAuthService().getUserEmail()
            drive = getDriveService(activityContext as Context, email)
        }

        canUse = result
        logger.logi("GoogleDriveService::askForAccess() ready = $canUse")

        // Notify about update
        callback?.onUserAction(null)
    }

    private fun getDriveService(context: Context, userEmail: String): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_APPDATA)
        ).setSelectedAccountName(userEmail)

        return Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Notes").build()
    }

}