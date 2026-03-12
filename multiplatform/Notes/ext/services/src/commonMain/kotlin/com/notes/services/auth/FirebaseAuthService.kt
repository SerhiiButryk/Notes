package com.notes.services.auth

import api.AppService
import api.Platform
import api.auth.AbstractAuthService
import api.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Service which implements authentication with Google Firebase server.
 */
open class FirebaseAuthService(
    protected val auth: com.google.firebase.auth.FirebaseAuth
) : AbstractAuthService() {

    private val tag = "FirebaseAuthService"

    override val key = AppService.FIREBASE_AUTH

    override suspend fun createUser(
        pass: String,
        email: String,
    ): AuthResult {
        return suspendCancellableCoroutine { continuation ->
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
    }

    override suspend fun login(
        pass: String,
        email: String,
        activityContext: Any?,
    ): AuthResult {
        val result =
            suspendCancellableCoroutine { continuation ->
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
            val afterResult = onLoginSuccessful(activityContext)
            // Stop!
            if (!afterResult.isSuccess()) return afterResult
            // Done
            callback?.onAuthCompleted(pass, getUserEmail())
        }
        return result
    }

    open suspend fun onLoginSuccessful(activityContext: Any?): AuthResult {
        return AuthResult.loginSuccess("")
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

    override suspend fun sendEmailVerify(): AuthResult {
        Platform().logger.logi("$tag::sendVerification()")
        return sendEmailVerify(null)
    }

    protected suspend fun sendEmailVerify(fireBaseUser: FirebaseUser?): AuthResult {
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

    override suspend fun verifyCode(code: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            auth
                .applyActionCode(code)
                .addOnSuccessListener {
                    Platform().logger.logi("$tag::verifyCode() success")
                    continuation.resume(true) { _, _, _ ->
                        // On cancellation do nothing
                    }
                }.addOnFailureListener { e ->
                    Platform().logger.loge("$tag::verifyCode() failed: ${e.message}")
                    continuation.resume(false) { _, _, _ ->
                        // On cancellation do nothing
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
        // Trying to mitigate this by waiting something and hoping google state gets refreshed
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

    override suspend fun changePassword(newPass: String): Boolean {
        Platform().logger.logi("$tag::changePassword()")

        val user = auth.currentUser
        if (user == null) {
            Platform().logger.loge("$tag::changePassword() user is null")
            return false
        }

        return suspendCancellableCoroutine { continuation ->
            user
                .updatePassword(newPass)
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