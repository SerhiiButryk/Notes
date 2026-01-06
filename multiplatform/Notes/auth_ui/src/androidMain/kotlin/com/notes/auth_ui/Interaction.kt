package com.notes.auth_ui

import com.notes.api.AuthResult
import com.notes.api.AuthService
import com.notes.api.provideAuthService
import com.notes.auth_ui.AuthViewModel.LoginUIState
import com.notes.auth_ui.AuthViewModel.RegisterUIState
import com.notes.auth_ui.data.saveUserEmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel

internal class Interaction(
    private val authService: AuthService = provideAuthService(),
) {
    private val coroutineContext = SupervisorJob() + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    suspend fun login(state: LoginUIState): AuthResult {
        if (state.password.isEmpty() || state.email.isEmpty()) {
            return AuthResult.emailOrPassEmpty(state.email)
        }

        val job =
            scope.async {
                authService.login(state.password, state.email)
            }

        return job.await()
    }

    suspend fun register(state: RegisterUIState): AuthResult {
        if (state.email.isEmpty() || state.password.isEmpty() || state.password != state.confirmPassword) {
            return AuthResult.passwordEmptyOrNotMatching(state.email)
        }

        val job =
            scope.async {
                authService.createUser(state.password, state.email)
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

    suspend fun getUserEmail(): String {
        val email =
            com.notes.auth_ui.data
                .getUserEmail()
        return email.ifEmpty { authService.getUserEmail() }
    }

    fun onClear() {
        scope.cancel()
    }
}
