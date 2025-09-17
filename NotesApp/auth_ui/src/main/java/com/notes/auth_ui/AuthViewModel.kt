package com.notes.auth_ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.auth.AuthResult
import com.notes.auth.AuthService
import com.notes.auth_ui.data.getRegisteredUserEmail
import com.notes.auth_ui.data.saveRegisteredUserEmail
import com.notes.api.PlatformAPIs.logger
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    open class UIState()

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class LoginUIState(
        val email: String = "",
        val password: String = "",
        val hasFocus: Boolean = false
    ) : UIState()

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class RegisterUIState(
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val hasFocus: Boolean = false
    ) : UIState()

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class DialogState(
        val title: String,
        val subtitle: String,
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState = _dialogState.asStateFlow()

    fun onShowLoginUI() {
        viewModelScope.launch {
            // Initially we are going to show a keyboard if ui is open
            _uiState.update { LoginUIState(hasFocus = true, email = getRegisteredUserEmail()) }
        }
    }

    fun onShowRegisterUI() {
        viewModelScope.launch {
            // Initially we are going to show a keyboard if ui is open
            _uiState.update { RegisterUIState(hasFocus = true) }
        }
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
        _uiState.update { (_uiState.value as LoginUIState).copy(email = getRegisteredUserEmail()) }
    }

}