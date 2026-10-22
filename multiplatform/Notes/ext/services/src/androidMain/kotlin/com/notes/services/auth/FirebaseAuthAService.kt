package com.notes.services.auth

import android.app.Activity
import api.AppService
import api.AppServices
import api.Platform
import api.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Service which implements authentication with Google Firebase server for Android.
 */
class FirebaseAuthAService(
    auth: com.google.firebase.auth.FirebaseAuth
) : FirebaseAuthService(auth) {

    private val tag = "FirebaseAuthAService"

    override suspend fun createUser(
        pass: String,
        email: String,
    ): AuthResult {
        val result = super.createUser(pass, email)
        return if (result.isSuccess()) {
            sendEmailVerify(fireBaseUser = auth.currentUser)
        } else {
            result
        }
    }

    override suspend fun login(
        tokenId: String,
        activityContext: Any?,
    ): AuthResult {
        Platform().logger.logi("$tag::login() requesting sign in with Google creds")
        val credential = GoogleAuthProvider.getCredential(tokenId, null)
        return suspendCancellableCoroutine { continuation ->
            auth
                .signInWithCredential(credential)
                .addOnCompleteListener(activityContext as Activity) { task ->
                    if (task.isSuccessful) {
                        Platform().logger.logi("$tag::login() logged in with Google token")
                        val email = task.result.user?.email ?: ""
                        continuation.resume(AuthResult.loginSuccess(email = email)) { _, _, _ ->
                            // no-op if coroutine is canceled
                        }
                    } else {
                        Platform().logger.loge("$tag::login() failed to sign in with credential")
                        continuation.resume(AuthResult.loginFailed()) { _, _, _ ->
                            // no-op if coroutine is canceled
                        }
                    }
                }
        }
    }

    override suspend fun onLoginSuccessful(activityContext: Any?): AuthResult {
        // User is authenticated
        // Now we can try signing in silently with Google account to get access to Google APIs
        return signInUsingGoogleSilent(activityContext)
    }

    private suspend fun signInUsingGoogleSilent(activityContext: Any?): AuthResult {
        if (activityContext != null) {
            Platform().logger.logi("$tag::signInUsingGoogleSilent() try to perform silent login")

            // Try to perform silent Google Sing In to get auth token
            // and then to finish firebase authentication

            val googleSignInService =
                AppServices
                    .getAuthServiceByKey(AppService.GOOGLE_AUTH) as GoogleSignInService

            val result = googleSignInService.login("", "", activityContext)

            if (result.isSuccess()) {
                Platform().logger.logi("$tag::signInUsingGoogleSilent() done silent login with Google service")
            } else {
                Platform().logger.loge("$tag::signInUsingGoogleSilent() silent login has failed")
            }
            return result
        } else {
            Platform().logger.logi("$tag::signInUsingGoogleSilent() can't perform silent login")
        }
        return AuthResult.loginFailed()
    }

}