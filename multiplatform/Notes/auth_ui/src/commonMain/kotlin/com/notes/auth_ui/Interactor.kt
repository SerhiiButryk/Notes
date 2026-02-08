package com.notes.auth_ui

import api.AppServices
import api.PlatformAPIs
import api.auth.AbstractAuthService
import api.auth.AuthResult
import com.notes.auth_ui.data.getUserEmail
import com.notes.auth_ui.data.saveUserEmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            return AuthResult.Companion.emailOrPassEmpty(email)
        }

        val job =
            scope.async {
                PlatformAPIs.crypto.addAuthCallbackFor(authService)
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

    suspend fun isEmailVerified(): Boolean {
        val job =
            scope.async {
                if (authService.isEmailVerified()) {
                    saveUserEmail(email = authService.getUserEmail())
                    true
                } else {
                    false
                }
            }
        return job.await()
    }

    suspend fun getEmail(): String {
        val email = getUserEmail()
        return email.ifEmpty { authService.getUserEmail() }
    }

    fun onClear() {
        scope.cancel()
    }

}