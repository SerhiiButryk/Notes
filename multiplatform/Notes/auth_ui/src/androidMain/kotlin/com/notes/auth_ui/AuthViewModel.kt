package com.notes.auth_ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.notes.api.AuthResult
import com.notes.api.AuthService
import com.notes.api.PlatformAPIs.logger
import com.notes.services.auth.FirebaseAuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

internal class AuthViewModel (
    authService: AuthService = FirebaseAuthService()
) : ViewModel() {

    open class UIState()

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class LoginUIState(
        val email: String = "",
        val password: String = "",
        val hasFocus: Boolean = false,
        val showProgress: Boolean = false
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

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    data class VerificationUIState(
        val emailVerificationSent: Boolean = false,
        val email: String = "",
        val isEmailVerified: Boolean = false
    ) : UIState() {

        val title =
            if (emailVerificationSent) "Verification email has been sent to you. " +
                    "Please, check your $email email."
            else "We failed to send verification email to you. Please, retry later."

        val subtitle =
            "Look for email with 'Verify your email for fancynotesdevtest' title. " +
                    "If you don't see such email check 'Spam' folder."
    }

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState = _dialogState.asStateFlow()

    private val interaction = Interaction(authService)

    fun login(state: LoginUIState, onSuccess: () -> Unit) {
        logger.logi("$TAG::login()")
        viewModelScope.launch {
            // Show progress
            val showProgress = (_uiState.value as LoginUIState).copy(showProgress = true)
            _uiState.emit(showProgress)
            val result = interaction.login(state)
            if (result.isSuccess()) {
                onSuccess()
            } else {
                handleResult(result)
            }
            // Hide progress
            val hideProgress = (_uiState.value as? LoginUIState)?.copy(showProgress = false)
            if (hideProgress != null) {
                _uiState.emit(showProgress)
            }
        }
    }

    fun register(state: RegisterUIState, onSuccess: suspend () -> Unit) {
        logger.logi("$TAG::register()")
        viewModelScope.launch {
            val result = interaction.register(state)
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

    fun runConfirmationCheck(shouldResendVerification: Boolean = false, navController: NavController) {
        logger.logi("$TAG::runConfirmationCheck()")
        viewModelScope.launch {
            val isEmailVerified = interaction.isEmailVerified()
            if (isEmailVerified) {
                logger.logi("$TAG::runConfirmationCheck() passed")
                onShowLoginUI()
                // Open login screen
                navController.navigate(Access)
            } else if (shouldResendVerification) {
                logger.logi("$TAG::runConfirmationCheck() resending verification")
                val result = interaction.sendEmailVerification()
                handleResult(result)
            } else {
                logger.logi("$TAG::runConfirmationCheck() failed, no-op")
            }
        }
    }

    fun onShowAuthUI() {
        viewModelScope.launch {
            if (interaction.getUserEmail().isEmpty()) {
                onShowRegisterUI()
            } else {
                onShowLoginUI()
            }
        }
    }

    private suspend fun onShowLoginUI() {
        // Initially we are going to show a keyboard if ui is open
        _uiState.emit(LoginUIState(hasFocus = true, email = interaction.getUserEmail()))
    }

    private suspend fun onShowVerificationUI() {
        val email = interaction.getUserEmail()
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
        interaction.onClear()
    }

    private suspend fun handleResult(result: AuthResult) {

        logger.logi("$TAG::handleResult()")

        // Handle success
        if (result.status == AuthResult.verificationSentOk) {
            _uiState.emit( (_uiState.value as VerificationUIState).copy(emailVerificationSent = true) )
            return
        }

        // Handle specific errors
        if (result.status == AuthResult.verificationSentError) {
            _uiState.emit( (_uiState.value as VerificationUIState).copy(emailVerificationSent = false) )
            return
        }

        // Handle other errors
        val strings = getErrorTitleAndMessage(result)
        val title = strings.first
        val subtitle = strings.second

        _dialogState.emit( DialogState(title = title, subtitle = subtitle) )
    }

}