package com.notes.auth_ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.notes.auth.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "AuthViewModel"

internal class AuthViewModel(
    private val authService: AuthService = AuthService()
) : ViewModel() {

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class LoginUIState(
        val email: String = "",
        val password: String = "",
        val emailHasFocus: Boolean = false
    )

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class RegisterUIState(
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val emailHasFocus: Boolean = false
    )

    private val _loginState = MutableStateFlow(LoginUIState())
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUIState())
    val registerState = _registerState.asStateFlow()

    init {
        // Initially we are going to show a keyboard if ui is open
        _loginState.value = LoginUIState(emailHasFocus = true)

        // Initially we are going to show a keyboard if ui is open
        _registerState.value = RegisterUIState(emailHasFocus = true)
    }

    fun login(loginUIState: LoginUIState) {
        authService.login(loginUIState.password, loginUIState.email)
    }

    fun register(registerUIState: RegisterUIState) {
        authService.register(
            registerUIState.password,
            registerUIState.confirmPassword,
            registerUIState.email
        )
    }

}