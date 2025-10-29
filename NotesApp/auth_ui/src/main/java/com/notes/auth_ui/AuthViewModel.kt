package com.notes.auth_ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.notes.api.AuthResult
import com.notes.api.AuthService
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
        viewModelScope.launch {
            val result = interaction.login(state)
            if (result.isSuccess()) {
                onSuccess()
            } else {
                handleResult(result)
            }
        }
    }

    fun register(state: RegisterUIState, onSuccess: suspend () -> Unit) {
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
        _uiState.update { LoginUIState(hasFocus = true, email = interaction.getUserEmail()) }
    }

    private suspend fun onShowVerificationUI() {
        val email = interaction.getUserEmail()
        _uiState.update { VerificationUIState(email = email) }
    }

    private fun onShowRegisterUI() {
        // Initially we are going to show a keyboard if ui is open
        _uiState.update { RegisterUIState(hasFocus = true) }
    }

    fun dismissDialog() {
        logger.logi("$TAG::dismissDialog()")
        _dialogState.update { null }
    }

    override fun onCleared() {
        logger.logi("$TAG::onCleared()")
        interaction.onClear()
    }

    private fun handleResult(result: AuthResult) {

        logger.logi("$TAG::handleResult()")

        // Handle success
        if (result.status == AuthResult.verificationSentOk) {
            _uiState.update { (_uiState.value as VerificationUIState).copy(emailVerificationSent = true) }
            return
        }

        // Handle specific errors
        if (result.status == AuthResult.verificationSentError) {
            _uiState.update { (_uiState.value as VerificationUIState).copy(emailVerificationSent = false) }
            return
        }

        // Handle other errors
        val strings = getErrorTitleAndMessage(result)
        val title = strings.first
        val subtitle = strings.second

        _dialogState.update { DialogState(title = title, subtitle = subtitle) }
    }

}