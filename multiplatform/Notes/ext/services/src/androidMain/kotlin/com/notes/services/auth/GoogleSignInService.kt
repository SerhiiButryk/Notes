package com.notes.services.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.NoCredentialException
import api.AppServices
import api.AuthService
import api.PlatformAPIs.logger
import api.auth.AuthResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class GoogleSignInService : AuthService {

    private var credentialManager: CredentialManager? = null
    @OptIn(ExperimentalAtomicApi::class)
    private val authenticated = AtomicBoolean(false)

    override val name: String = "google"

    override fun init(context: Any?) {
        credentialManager = CredentialManager.create(context as Context)
    }

    override suspend fun login(pass: String, email: String, activityContext: Any?): AuthResult {
        logger.logi("GoogleSignInService::login()")

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId("411307947225-6rlasngq8a1is80pq5tt3ang6hjpen9o.apps.googleusercontent.com")
            .setAutoSelectEnabled(false)
            .setFilterByAuthorizedAccounts(false) // Allow all Google accounts on the device to show up
            // nonce string to use when generating a Google ID token might be usefully
            // .setNonce(nonce)
            .build()

        // Create the Credential Manager request
        val request = androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = try {
            credentialManager!!.getCredential(
                request = request,
                context = activityContext as Context,
            )
        } catch (e: NoCredentialException) {
            logger.logi("GoogleSignInService::login() error: $e")
            return AuthResult.loginFailed()
        }

        return handleResponse(result.credential, activityContext)
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
                .getDefaultAuthService()
                .login(idToken, activityContext)
            if (result.isSuccess()) {
                authenticated.store(true)
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

    override suspend fun signOut(): Boolean {
        logger.logi("GoogleSignInService::signOut()")
        // Firebase sign out
        AppServices
            .getDefaultAuthService()
            .signOut()

        try {
            val clearRequest = ClearCredentialStateRequest()
            credentialManager?.clearCredentialState(clearRequest)
            return true
        } catch (e: ClearCredentialException) {
            logger.loge("GoogleSignInService::signOut() Couldn't clear user credentials: ${e.localizedMessage}")
        }

        return false
    }
}