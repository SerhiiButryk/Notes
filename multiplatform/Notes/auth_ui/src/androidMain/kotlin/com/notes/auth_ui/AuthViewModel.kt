package com.notes.auth_ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import api.auth.AuthResult
import api.PlatformAPIs.logger
import api.getErrorTitleAndMessage
import api.getVerifyTitleAndMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {
    open class UIState

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class LoginUIState(
        val email: String = "",
        val password: String = "",
        val hasFocus: Boolean = false,
        val showProgress: Boolean = false,
        val uiForced: Boolean = true
    ) : UIState()

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class RegisterUIState(
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val hasFocus: Boolean = false,
    ) : UIState()

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class DialogState(
        val title: String,
        val subtitle: String,
    )

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class VerificationUIState(
        val emailVerificationSent: Boolean = false,
        val email: String = "",
        val isEmailVerified: Boolean = false,
    ) : UIState() {
        val title =
            getVerifyTitleAndMessage(emailVerificationSent, email)
                .first
        val subtitle =
            getVerifyTitleAndMessage(emailVerificationSent, email)
                .second
    }

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
        logger.logi("$TAG::login()")
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
        logger.logi("$TAG::register()")
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
        logger.logi("$TAG::runConfirmationCheck()")
        viewModelScope.launch {
            val isEmailVerified = interactor.isEmailVerified()
            if (isEmailVerified) {
                logger.logi("$TAG::runConfirmationCheck() passed")
                onShowLoginUI()
                // Open login screen
                navController.navigate(Access)
            } else if (shouldResendVerification) {
                logger.logi("$TAG::runConfirmationCheck() resending verification")
                val result = interactor.sendEmailVerification()
                handleResult(result)
            } else {
                logger.logi("$TAG::runConfirmationCheck() failed, no-op")
            }
        }
    }

    fun onShowAuthUI(uiForced: Boolean = false) {
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
        logger.logi("$TAG::dismissDialog()")
        viewModelScope.launch {
            _dialogState.emit(null)
        }
    }

    override fun onCleared() {
        logger.logi("$TAG::onCleared()")
        interactor.onClear()
    }

    private suspend fun handleResult(result: AuthResult) {
        logger.logi("$TAG::handleResult()")

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
