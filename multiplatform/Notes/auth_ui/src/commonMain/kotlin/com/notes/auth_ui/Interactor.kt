package com.notes.auth_ui

import api.AppServices
import api.Platform
import api.auth.AbstractAuthService
import api.auth.AuthResult
import api.data.getUserEmail
import api.data.saveUserEmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel

internal class Interactor(
    private val authService: AbstractAuthService = AppServices.getDefaultAuthService()!!,
) {
    private val coroutineContext = SupervisorJob() + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    suspend fun login(password: String, email: String, context: Any?): AuthResult {
        if (password.isEmpty() || email.isEmpty()) {
            return AuthResult.emailOrPassEmpty(email)
        }

        val job =
            scope.async {
                Platform().crypto.addAuthCallbackFor(authService)
                val result = authService.login(password, email, context)
                // Save user email if it's not saved
                // It's the case if user didn't register and did sing in with
                // an existed account
                val emailStored = getUserEmail()
                if (emailStored.isEmpty() && result.isSuccess()) {
                    saveUserEmail(email = result.email)
                }
                result
            }

        return job.await()
    }

    suspend fun register(confirmPassword: String, password: String, email: String): AuthResult {

        if (email.isEmpty() || password.isEmpty() || password != confirmPassword) {
            return AuthResult.Companion.passwordEmptyOrNotMatching(email)
        }

        val job =
            scope.async {
                authService.createUser(password, email)
            }

        return job.await()
    }

    suspend fun sendEmailVerification(): AuthResult {
        val job = scope.async { authService.sendEmailVerify() }
        return job.await()
    }

    suspend fun verifyCode(code: String): Boolean {
        val job = scope.async { authService.verifyCode(code) }
        return job.await()
    }

    suspend fun isEmailVerified(): Boolean {
        val job =
            scope.async {
                if (authService.isEmailVerified()) {
                    saveUserEmail(email = authService.getUserEmail())
                    Platform().logger.logi("isEmailVerified(): email is verified")
                    true
                } else {
                    Platform().logger.loge("isEmailVerified(): email is not verified")
                    false
                }
            }
        return job.await()
    }

    suspend fun getEmail(): String {
        val email = getUserEmail()
        return email.ifEmpty { authService.getUserEmail() }
    }

        suspend fun changePassword(newPass: String): Boolean {
        val job = scope.async {

            // When the password change is successful we must update derived key.
            // Derived key we use to encrypt user notes before they are sent to datastore.
            // This will update derived key, However, there are coner case which can corrupt user data:

            // 1. What happens if local notes are not up-to-date with remote datastore services ?
            // 2. What happens if local notes are up-to-date with one datastore, but out of sync with others ?

            //TODO Should change password if only we absolutely sure that user local
            // data is up-to-data with remote
            // datastore's so we can encrypt and override them using new key

            // We should create a backup before changing a password

            val result = authService.changePassword(newPass)
            if (result) {
                // Update derived key
                Platform().crypto.onLoginCompleted(newPass, getEmail(), true)
                // Update data and resave it using a new key
                Platform().appRepo.triggerSyncWithRemote()
            }

            result
        }
        return job.await()
    }

    fun onClear() {
        scope.cancel()
    }

}