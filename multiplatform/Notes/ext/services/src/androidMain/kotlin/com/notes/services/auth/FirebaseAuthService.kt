package com.notes.services.auth

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import api.auth.AuthResult
import api.AuthService
import api.PlatformAPIs.logger
import api.auth.AuthCallback
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Service which implements authentication with Google Firebase server.
 */
class FirebaseAuthService : AuthService {
    private val tag = "FirebaseAuthService"

    private val auth: FirebaseAuth = Firebase.auth
    private var callback: AuthCallback? = null

    override fun setAuthCallback(callback: AuthCallback) {
        this.callback = callback
    }

    override suspend fun createUser(
        pass: String,
        email: String,
    ): AuthResult {
        val result =
            suspendCancellableCoroutine { continuation ->
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        logger.logi("$tag::createUser() success")
                        continuation.resume(AuthResult.registrationSuccess(email)) { _, _, _ ->
                            // no-op if coroutine is cancelled
                        }
                    } else {
                        logger.loge("$tag::createUser() failure: ${task.exception}")
                        continuation.resume(AuthResult.registrationFailed(email)) { _, _, _ ->
                            // no-op if coroutine is cancelled
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
    ): AuthResult {
        val result = suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logger.logi("$tag::login() success")
                    continuation.resume(AuthResult.loginSuccess(email = email)) { _, _, _ ->
                        // no-op if coroutine is cancelled
                    }
                } else {
                    logger.loge("$tag::login() failure: ${task.exception}")
                    continuation.resume(AuthResult.loginFailed()) { _, _, _ ->
                        // no-op if coroutine is cancelled
                    }
                }
            }
        }
        if (result.isSuccess()) {
            callback?.onLoginCompleted(pass, getUserId())
        }
        return result
    }

    override suspend fun sendEmailVerify(): AuthResult {
        logger.logi("$tag::sendVerification()")
        return sendEmailVerify(null)
    }

    override suspend fun isEmailVerified(): Boolean {
        val user: FirebaseUser? = auth.currentUser
        if (user == null) {
            logger.logi("$tag::isEmailVerified() user is null")
            return false
        }
        suspendCancellableCoroutine { continuation ->
            // TODO: Check how callback gets called
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logger.logi("$tag::isEmailVerified() reloaded data")
                    continuation.resume(true) { _, _, _ ->
                        // On cancellation do nothing
                    }
                } else {
                    logger.loge("$tag::isEmailVerified() reload is failed: ${task.exception}")
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

    private suspend fun sendEmailVerify(fireBaseUser: FirebaseUser?): AuthResult {
        logger.logi("$tag::sendVerification()")
        val user: FirebaseUser? = fireBaseUser ?: auth.currentUser
        return suspendCancellableCoroutine { continuation ->
            user?.sendEmailVerification()?.addOnCompleteListener { task ->
                logger.logi("$tag::sendVerification() completed")
                if (task.isSuccessful) {
                    logger.logi("$tag::sendVerification() verification code is sent")
                    continuation.resume(
                        AuthResult.verificationSentSuccess(user.email!!),
                    ) { _, _, _ ->
                        // no-op if coroutine is cancelled
                    }
                } else {
                    logger.loge("$tag::sendVerification() failure: ${task.exception}")
                    continuation.resume(
                        AuthResult.verificationSentFailed(user.email!!),
                    ) { _, _, _ ->
                        // no-op if coroutine is cancelled
                    }
                }
            } ?: continuation.resume(
                AuthResult.verificationSentFailed(""),
            ) { _, _, _ ->
                // no-op if coroutine is cancelled
            }
        }
    }
}
