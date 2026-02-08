package com.notes.services.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.NoCredentialException
import api.AppServices
import api.PlatformAPIs.logger
import api.auth.AbstractAuthService
import api.auth.AuthResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.notes.services.storage.GoogleDriveService
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Service which implements Google Sing In authentication.
 */
class GoogleSignInService : AbstractAuthService() {

    private var credentialManager: CredentialManager? = null
    @OptIn(ExperimentalAtomicApi::class)
    private val authenticated = AtomicBoolean(false)

    override val name: String = "google"

    // Whether to select account automatically if it's possible
    private var autoSelectEnabled = false
    private var filterByAuthorizedAccounts = false

    override fun init(context: Any?) {
        credentialManager = CredentialManager.create(context as Context)
    }

    fun setAutoSelectEnabled(autoSelectEnabled: Boolean) {
        this.autoSelectEnabled = autoSelectEnabled
    }

    fun setFilterByAuthorizedAccounts(filterByAuthorizedAccounts: Boolean) {
        this.filterByAuthorizedAccounts = filterByAuthorizedAccounts
    }

    override fun resetSettings() {
        setFilterByAuthorizedAccounts(false)
        setAutoSelectEnabled(false)
    }

    override suspend fun login(pass: String, email: String, activityContext: Any?): AuthResult {
        logger.logi("GoogleSignInService::login()")

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(AppServices.serverClientId)
            .setAutoSelectEnabled(autoSelectEnabled)
            // Allow all Google accounts on the device to show up
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            // nonce string to use when generating a Google ID token might be usefully
            // .setNonce(nonce)
            .build()

        // Create the Credential Manager request
        val request = androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response = try {
            credentialManager!!.getCredential(
                request = request,
                context = activityContext as Context,
            )
        } catch (e: NoCredentialException) {
            logger.logi("GoogleSignInService::login() error: $e")
            return AuthResult.loginFailed()
        }

        return handleResponse(response.credential, activityContext)
    }

    @OptIn(ExperimentalAtomicApi::class)
    private suspend fun handleResponse(credential: Credential, activityContext: Any?): AuthResult {
        logger.logi("GoogleSignInService::handleResponse()")
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            // Sign in to Firebase using the token
            val idToken = googleIdTokenCredential.idToken
            val result = AppServices
                .getDefaultAuthService()!!
                .login(idToken, activityContext)

            if (result.isSuccess()) {

                authenticated.store(true)

                requestGoogleDriveAccess(activityContext)

                val email = AppServices
                    .getDefaultAuthService()!!
                    .getUserEmail()

                return AuthResult.loginSuccess(email)
            }
        } else {
            logger.loge("GoogleSignInService::handleResponse() wrong credentials")
        }
        return AuthResult.loginFailed()
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun isAuthenticated(): Boolean {
        return authenticated.load()
    }

    @OptIn(ExperimentalAtomicApi::class)
    override suspend fun signOut(): Boolean {
        logger.logi("GoogleSignInService::signOut()")
        // Firebase sign out
        AppServices
            .getDefaultAuthService()!!
            .signOut()
        authenticated.store(false)
        try {
            val clearRequest = ClearCredentialStateRequest()
            credentialManager?.clearCredentialState(clearRequest)
            return true
        } catch (e: ClearCredentialException) {
            logger.loge("GoogleSignInService::signOut() Couldn't clear user credentials: ${e.localizedMessage}")
        }

        return false
    }

    // This should show a dialog for a user to confirm permissions
    private suspend fun requestGoogleDriveAccess(activityContext: Any?) {
        val googleDriveService = AppServices
            .__delicateCall_getOriginalStoreService("googledrive") as GoogleDriveService
        googleDriveService.askForAccess(activityContext, callback)
    }
}