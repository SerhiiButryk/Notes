package com.notes.auth_ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {

    data class LoginUIState(
        val email: String = "",
        val password: String = "",
        val emailHasFocus: Boolean = false
    )

    private val _loginState = MutableStateFlow(LoginUIState())
    val loginState = _loginState.asStateFlow()

    init {
        // Initially we are going to show a keyboard if login ui is open
        _loginState.value = LoginUIState(emailHasFocus = true)
    }

}