package com.notes.auth_ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.auth.AuthResult
import com.notes.auth.AuthService
import com.notes.auth_ui.data.getRegisteredUserEmail
import com.notes.auth_ui.data.saveRegisteredUserEmail
import com.notes.interfaces.PlatformAPIs.logger
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
        val hasFocus: Boolean = false
    )

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class RegisterUIState(
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val hasFocus: Boolean = false
    )

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class DialogState(
        val title: String,
        val subtitle: String,
    )

    private val _loginState = MutableStateFlow(LoginUIState())
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUIState())
    val registerState = _registerState.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState = _dialogState.asStateFlow()

    init {

        viewModelScope.launch {
            // Initially we are going to show a keyboard if ui is open
            _loginState.value = LoginUIState(hasFocus = true, email = getRegisteredUserEmail())
        }

        // Initially we are going to show a keyboard if ui is open
        _registerState.value = RegisterUIState(hasFocus = true)
    }

    fun login(loginUIState: LoginUIState, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authService.login(loginUIState.password, loginUIState.email) { result ->
                // Handle the result
                viewModelScope.launch {
                    if (result.isSuccess()) {
                        onSuccess()
                    } else {
                        handleError(result)
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
                // Handle the result
                viewModelScope.launch {
                    if (result.isSuccess()) {
                        saveRegisteredUserEmail(result.email)
                        refreshUserEmail()
                        onSuccess()
                    } else {
                        handleError(result)
                    }
                }
            }
        }
    }

    fun dismissDialog() {
        logger.logi("dismissDialog()")
        _dialogState.value = null
    }

    private fun handleError(result: AuthResult) {

        logger.logi("handleError() showing a dialog")

        val strings = getErrorTitleAndMessage(result)
        val title = strings.first
        val subtitle = strings.second

        _dialogState.value = DialogState(title = title, subtitle = subtitle)
    }

    private suspend fun refreshUserEmail() {
        _loginState.value = _loginState.value.copy(email = getRegisteredUserEmail())
    }

}