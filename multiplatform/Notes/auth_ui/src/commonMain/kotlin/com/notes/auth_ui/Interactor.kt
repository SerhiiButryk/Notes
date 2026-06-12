package com.notes.auth_ui

import api.AppServices
import api.Platform
import api.auth.AbstractAuthService
import api.auth.AuthResult
import api.data.getRegisteredUser
import api.data.setRegisteredUser
import api.data.userDataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel

class Interactor(
    private val authService: AbstractAuthService = AppServices.getDefaultAuthService(),
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
                val emailStored = getRegisteredUser()
                if (emailStored.isEmpty() && result.isSuccess()) {
                    setRegisteredUser(email = result.email)
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

    private suspend fun verifyCode(code: String): Boolean {
        val job = scope.async { authService.verifyCode(code) }
        return job.await()
    }

    suspend fun verifyCode() {
        if (userDataState.value.code.isNotEmpty()) {
            verifyCode(userDataState.value.code)
        }
    }

    suspend fun isEmailVerified(): Boolean {
        val job =
            scope.async {
                if (authService.isEmailVerified()) {
                    setRegisteredUser(email = authService.getUserEmail())
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
        val email = getRegisteredUser()
        return email.ifEmpty { authService.getUserEmail() }
    }

    suspend fun hasRegisteredUser() = getRegisteredUser().isNotEmpty()

    suspend fun changePassword(newPass: String): Boolean {
        val job = scope.async {

            // When user changes the password we must update derived key.
            // Derived key we use to encrypt user data before they are sent to datastore.
            // This will update derived key. However, there are coner case which can corrupt user data:

            // 1. What happens if local data are not up-to-date with remote datastore services ?
            // 2. What happens if local data are up-to-date with one datastore, but out of sync with others ?

            // We should create a backup file before changing a password to mitigate user data corruption

            if (!Platform().appRepo.canChangePassword())
                return@async false

            val result = authService.changePassword(newPass)

            if (result) {

                // Update derived key
                Platform().crypto.onAuthCompleted(newPass, getEmail(), true)

                Platform().appRepo.onPasswordChanged()
            }

            result
        }
        return job.await()
    }

    fun onClear() {
        scope.cancel()
    }

}