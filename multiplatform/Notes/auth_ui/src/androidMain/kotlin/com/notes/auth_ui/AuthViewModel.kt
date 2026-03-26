package com.notes.auth_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import api.Platform
import api.auth.AuthResult
import api.data.userDataState
import api.utils.getErrorTitleAndMessage
import com.notes.auth_ui.ui.DialogState
import com.notes.auth_ui.ui.LoginUIState
import com.notes.auth_ui.ui.RegisterUIState
import com.notes.auth_ui.ui.UIState
import com.notes.auth_ui.ui.VerificationUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.notes.ui.Access

private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState = _dialogState.asStateFlow()

    private val interactor = Interactor()

    fun <T> MutableStateFlow<T>.copyLoginUIState(showProgress: Boolean): UIState? {
        return (value as? LoginUIState)?.copy(showProgress = showProgress)
    }

    fun login(
        state: LoginUIState,
        onSuccess: () -> Unit,
        context: Any?
    ) {
        Platform().logger.logi("$TAG::login()")
        viewModelScope.launch {
            // Show progress
            val newState = _uiState.copyLoginUIState(showProgress = true)
            _uiState.emit(newState!!)

            val result = interactor.login(state.password, state.email, context)
            if (result.isSuccess()) {
                onSuccess()
            } else {
                handleResult(result)
            }

            val updateProgress = _uiState.copyLoginUIState(showProgress = result.isSuccess())
            if (updateProgress != null) {
                _uiState.emit(updateProgress)
            }
        }
    }

    fun register(
        state: RegisterUIState,
        onSuccess: suspend () -> Unit,
    ) {
        Platform().logger.logi("$TAG::register()")
        viewModelScope.launch {
            val result = interactor.register(state.confirmPassword, state.password, state.email)
            if (result.isSuccess()) {
                onShowVerificationUI()
                // Handle possible errors
                handleResult(result)
                onSuccess()
            } else {
                handleResult(result)
            }
        }
    }

    fun runConfirmationCheck(
        shouldResendVerification: Boolean = false,
        navController: NavController,
    ) {
        Platform().logger.logi("$TAG::runConfirmationCheck()")
        viewModelScope.launch {
            val isEmailVerified = interactor.isEmailVerified()
            if (isEmailVerified) {
                Platform().logger.logi("$TAG::runConfirmationCheck() passed")
                onShowLoginUI()
                // Open login screen
                navController.navigate(Access)
            } else if (shouldResendVerification) {
                Platform().logger.logi("$TAG::runConfirmationCheck() resending verification")
                val result = interactor.sendEmailVerification()
                handleResult(result)
            } else {
                Platform().logger.logi("$TAG::runConfirmationCheck() failed, no-op")
            }
        }
    }

    fun onShowAccessUI(uiForced: Boolean = false) {
        viewModelScope.launch {
            if (uiForced) {
                onShowLoginUI(uiForced)
                return@launch
            }
            // Special case to handle if user don't want to register
            // and to allow he/she to go to sing in ui from register ui
            if (_uiState.value is LoginUIState) {
                if ((_uiState.value as LoginUIState).uiForced) {
                    return@launch
                }
            }
            if (interactor.getEmail().isEmpty()) {
                onShowRegisterUI()
            } else {
                onShowLoginUI()
            }
        }
    }

    fun onShowOnBoardingUI(navController: NavController) {
        viewModelScope.launch {
            if (userDataState.value.code.isNotEmpty()) {
                if (interactor.verifyCode(userDataState.value.code)) {
                    // Check if can proceed
                    runConfirmationCheck(navController = navController)
                }
            }
        }
    }

    private suspend fun onShowLoginUI(uiForced: Boolean = false) {
        // Initially we are going to show a keyboard if ui is open
        val newState = LoginUIState(hasFocus = true, email = interactor.getEmail(), uiForced = uiForced)
        _uiState.emit(newState)
    }

    private suspend fun onShowVerificationUI() {
        val email = interactor.getEmail()
        _uiState.emit(VerificationUIState(email = email))
    }

    private suspend fun onShowRegisterUI() {
        // Initially we are going to show a keyboard if ui is open
        _uiState.emit(RegisterUIState(hasFocus = true))
    }

    fun dismissDialog() {
        Platform().logger.logi("$TAG::dismissDialog()")
        viewModelScope.launch {
            _dialogState.emit(null)
        }
    }

    override fun onCleared() {
        Platform().logger.logi("$TAG::onCleared()")
        interactor.onClear()
    }

    private suspend fun handleResult(result: AuthResult) {
        Platform().logger.logi("$TAG::handleResult()")

        if (result.isEmailVerificationPassed()) {
            _uiState.emit(
                (_uiState.value as VerificationUIState)
                    .copy(emailVerificationSent = true)
            )
            return
        }

        if (result.isEmailVerificationFailed()) {
            _uiState.emit(
                (_uiState.value as VerificationUIState)
                    .copy(emailVerificationSent = false)
            )
            return
        }

        // Handle other errors
        val strings = getErrorTitleAndMessage(result)
        val title = strings.first
        val subtitle = strings.second

        _dialogState.emit(DialogState(title = title, subtitle = subtitle))
    }
}
