package com.notes.auth_ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.auth.AuthService
import com.notes.auth_ui.data.saveRegisteredUserEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val authService: AuthService
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

    fun login(loginUIState: LoginUIState, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authService.login(loginUIState.password, loginUIState.email) { result ->
                // TODO handle errors
                if (result.isSuccess()) {
                    viewModelScope.launch {
                        onSuccess()
                    }
                }
            }
        }
    }

    fun register(registerUIState: RegisterUIState, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authService.register(
                registerUIState.password,
                registerUIState.confirmPassword,
                registerUIState.email
            ) { result ->
                // TODO handle errors
                if (result.isSuccess()) {
                    viewModelScope.launch {
                        saveRegisteredUserEmail(result.email)
                        onSuccess()
                    }
                }
            }
        }
    }

}