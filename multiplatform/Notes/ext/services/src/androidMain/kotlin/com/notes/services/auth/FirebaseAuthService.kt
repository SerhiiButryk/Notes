package com.notes.services.auth

import android.app.Activity
import api.AppServices
import api.Platform
import api.auth.AbstractAuthService
import api.auth.AuthResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Service which implements authentication with Google Firebase server.
 */
class FirebaseAuthService : AbstractAuthService() {

    private val tag = "FirebaseAuthService"

    private val auth: FirebaseAuth = Firebase.auth

    override val name: String = "firebase"

    override suspend fun createUser(
        pass: String,
        email: String,
    ): AuthResult {
        val result =
            suspendCancellableCoroutine { continuation ->
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Platform().logger.logi("$tag::createUser() success")
                        continuation.resume(AuthResult.registrationSuccess(email)) { _, _, _ ->
                            // no-op if coroutine is canceled
                        }
                    } else {
                        Platform().logger.loge("$tag::createUser() failure: ${task.exception}")
                        continuation.resume(AuthResult.registrationFailed(email)) { _, _, _ ->
                            // no-op if coroutine is canceled
                        }
                    }
                }
            }
        return if (result.isSuccess()) {
            sendEmailVerify(fireBaseUser = auth.currentUser)
        } else {
            result
        }
    }

    override suspend fun login(
        pass: String,
        email: String,
        activityContext: Any?
    ): AuthResult {
        val result = suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Platform().logger.logi("$tag::login() success")
                    continuation.resume(AuthResult.loginSuccess(email = email)) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                } else {
                    Platform().logger.loge("$tag::login() failure: ${task.exception}")
                    continuation.resume(AuthResult.loginFailed()) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                }
            }
        }
        if (result.isSuccess()) {
            // User is authenticated
            // Now we can try sign in silently with Google account to get access to Google APIs
            signInUsingGoogleSilent(activityContext)
            // Done
            callback?.onLoginCompleted(pass, getUserId(), false)
        }
        return result
    }

    override suspend fun login(tokenId: String, activityContext: Any?): AuthResult {
        Platform().logger.logi("$tag::login() requesting sign with Google creds")

        val credential = GoogleAuthProvider.getCredential(tokenId, null)

        val result = suspendCancellableCoroutine { continuation ->
            auth.signInWithCredential(credential)
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

        return result
    }

    override suspend fun sendEmailVerify(): AuthResult {
        Platform().logger.logi("$tag::sendVerification()")
        return sendEmailVerify(null)
    }

    override suspend fun verifyCode(code: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            auth.applyActionCode(code)
                .addOnSuccessListener {
                    Platform().logger.logi("$tag::verifyCode() success")
                    continuation.resume(true) { _, _, _ ->
                        // On cancellation do nothing
                    }
                }
                .addOnFailureListener { e ->
                    Platform().logger.loge("$tag::verifyCode() failed: ${e.message}")
                    continuation.resume(false) { _, _, _ ->
                        // On cancellation do nothing
                    }
                }
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        val user: FirebaseUser? = auth.currentUser
        if (user == null) {
            Platform().logger.logi("$tag::isEmailVerified() user is null")
            return false
        }
        // Sometimes we don't get a precise response from the google
        // Trying to mitigate this by using a delay
        delay(3000)
        suspendCancellableCoroutine { continuation ->
            // TODO: Check how callback gets called
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Platform().logger.logi("$tag::isEmailVerified() reloaded data")
                    continuation.resume(true) { _, _, _ ->
                        // On cancellation do nothing
                    }
                } else {
                    Platform().logger.loge("$tag::isEmailVerified() reload is failed: ${task.exception}")
                    continuation.resume(false) { _, _, _ ->
                        // On cancellation do nothing
                    }
                }
            }
        }
        return user.isEmailVerified
    }

    override fun getUserEmail(): String = auth.currentUser?.email ?: ""

    override fun isAuthenticated(): Boolean = auth.currentUser?.uid != null

    override fun getUserId(): String {
        if (auth.currentUser?.uid == null) {
            return ""
        }
        return auth.currentUser?.uid!!
    }

    override suspend fun signOut(): Boolean {
        auth.signOut()
        return true
    }

    private suspend fun sendEmailVerify(fireBaseUser: FirebaseUser?): AuthResult {
        Platform().logger.logi("$tag::sendVerification()")
        val user: FirebaseUser? = fireBaseUser ?: auth.currentUser
        return suspendCancellableCoroutine { continuation ->
            user?.sendEmailVerification()?.addOnCompleteListener { task ->
                Platform().logger.logi("$tag::sendVerification() completed")
                if (task.isSuccessful) {
                    Platform().logger.logi("$tag::sendVerification() verification code is sent")
                    continuation.resume(
                        AuthResult.verificationSentSuccess(user.email!!),
                    ) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                } else {
                    Platform().logger.loge("$tag::sendVerification() failure: ${task.exception}")
                    continuation.resume(
                        AuthResult.verificationSentFailed(user.email!!),
                    ) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                }
            } ?: continuation.resume(
                AuthResult.verificationSentFailed(""),
            ) { _, _, _ ->
                // no-op if coroutine is cancelled
            }
        }
    }

    private suspend fun signInUsingGoogleSilent(activityContext: Any?) {
        if (activityContext != null) {
            Platform().logger.logi("$tag::signInUsingGoogleSilent() try to perform silent login")

            // Try to perform silent Google Sing In to get auth token
            // and then to finish firebase authentication

            val googleSignInService = AppServices
                .getAuthServiceByName("google") as GoogleSignInService

            googleSignInService.setAutoSelectEnabled(true)
            googleSignInService.setFilterByAuthorizedAccounts(true)

            val result = googleSignInService.login("", "", activityContext)

            if (result.isSuccess()) {
                Platform().logger.logi("$tag::signInUsingGoogleSilent() done silent login with Google service")
            } else {
                Platform().logger.loge("$tag::signInUsingGoogleSilent() silent login has failed")
            }
        } else {
            Platform().logger.logi("$tag::signInUsingGoogleSilent() can't perform silent login")
        }
    }

    override suspend fun changePassword(newPass: String): Boolean {
        Platform().logger.logi("$tag::changePassword()")

        val user = auth.currentUser
        if (user == null) {
            Platform().logger.loge("$tag::changePassword() user is null")
            return false
        }

        return suspendCancellableCoroutine { continuation ->
            user.updatePassword(newPass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Platform().logger.logi("$tag::changePassword() done")
                        continuation.resume(true) { _, _, _ ->
                            // no-op if coroutine is canceled
                        }
                    } else {
                        Platform().logger.loge("$tag::changePassword() failed, may need to reauth user")
                        continuation.resume(false) { _, _, _ ->
                            // no-op if coroutine is canceled
                        }
                    }
                }
        }
    }
}
