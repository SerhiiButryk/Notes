package com.notes.auth_ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.api.PlatformAPIs.logger
import com.notes.auth.AuthResult
import com.notes.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    authService: AuthService
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

    private val interaction = Interaction(authService)

    fun onShowLoginUI() {
        viewModelScope.launch {
            // Initially we are going to show a keyboard if ui is open
            _uiState.update { LoginUIState(hasFocus = true, email = interaction.getUserEmail()) }
        }
    }

    fun onShowRegisterUI() {
        viewModelScope.launch {
            // Initially we are going to show a keyboard if ui is open
            _uiState.update { RegisterUIState(hasFocus = true) }
        }
    }

    fun login(state: LoginUIState, onSuccess: () -> Unit) {
        interaction.login(state, onSuccess, onError = { handleError(it) })
    }

    fun register(state: RegisterUIState, onSuccess: suspend () -> Unit) {
        interaction.register(state, onSuccess = {
            refreshUserEmail()
            onSuccess()
        }, onError = { handleError(it) })
    }

    fun dismissDialog() {
        logger.logi("$TAG::dismissDialog()")
        _dialogState.value = null
    }

    override fun onCleared() {
        logger.logi("$TAG::onCleared()")
        interaction.onClear()
    }

    private fun handleError(result: AuthResult) {

        logger.logi("$TAG::handleError() showing a dialog")

        val strings = getErrorTitleAndMessage(result)
        val title = strings.first
        val subtitle = strings.second

        _dialogState.value = DialogState(title = title, subtitle = subtitle)
    }

    private suspend fun refreshUserEmail() {
        val email = interaction.getUserEmail()
        _uiState.update { (_uiState.value as LoginUIState).copy(email = email) }
    }

}