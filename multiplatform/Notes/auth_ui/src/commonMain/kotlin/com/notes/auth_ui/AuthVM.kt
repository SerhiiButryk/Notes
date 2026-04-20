package com.notes.auth_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.Platform
import com.notes.auth_ui.data.LoginUIState
import com.notes.auth_ui.data.RegisterUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val tag = "AuthVM"

class AuthVM(
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = scopeOverride ?: viewModelScope

    private val _uiStateRegister = MutableStateFlow(RegisterUIState())
    val registerUIState = _uiStateRegister.asStateFlow()

    private val _uiStateLogin = MutableStateFlow(LoginUIState())
    val loginUIState = _uiStateLogin.asStateFlow()

    init {

        scope.launch {
            onShowRegisterScreen()
            onShowLoginScreen()
        }

    }

    fun login(
        state: LoginUIState,
        onSuccess: () -> Unit,
    ) {

        Platform().logger.logi("login()")

        onSuccess()
    }

    fun register(
        state: RegisterUIState,
        onSuccess: () -> Unit,
    ) {

        Platform().logger.logi("register()")

        onSuccess()
    }

    suspend fun onShowRegisterScreen() {
        val newState = RegisterUIState(hasFocus = true)
        _uiStateRegister.emit(newState)
    }

    suspend fun onShowLoginScreen() {
        val newState = LoginUIState(hasFocus = true, email = "")
        _uiStateLogin.emit(newState)
    }

}