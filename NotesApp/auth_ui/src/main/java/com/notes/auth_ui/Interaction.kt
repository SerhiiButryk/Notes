package com.notes.auth_ui

import com.notes.auth.AuthResult
import com.notes.auth.AuthService
import com.notes.auth_ui.AuthViewModel.LoginUIState
import com.notes.auth_ui.AuthViewModel.RegisterUIState
import com.notes.auth_ui.data.getRegisteredUserEmail
import com.notes.auth_ui.data.saveRegisteredUserEmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class Interaction(private val authService: AuthService) {

    private val coroutineContext = SupervisorJob() + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    fun login(state: LoginUIState, onSuccess: () -> Unit, onError: (AuthResult) -> Unit) {
        scope.launch {
            authService.login(state.password, state.email) { result ->
                // Handle the result
                scope.launch(Dispatchers.Main.immediate) {
                    if (result.isSuccess()) {
                        onSuccess()
                    } else {
                        onError(result)
                    }
                }
            }
        }
    }

    fun register(state: RegisterUIState, onSuccess: suspend () -> Unit, onError: (AuthResult) -> Unit) {
        scope.launch {
            authService.register(
                state.password,
                state.confirmPassword,
                state.email
            ) { result ->
                // Handle the result
                scope.launch(Dispatchers.Main.immediate) {
                    if (result.isSuccess()) {
                        saveRegisteredUserEmail(result.email)
                        onSuccess()
                    } else {
                        onError(result)
                    }
                }
            }
        }
    }

    suspend fun getUserEmail(): String {
        return getRegisteredUserEmail()
    }

    fun onClear() {
        scope.cancel()
    }

}