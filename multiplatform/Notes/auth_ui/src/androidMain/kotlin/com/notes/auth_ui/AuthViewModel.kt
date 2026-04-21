package com.notes.auth_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import api.Platform
import api.auth.AuthResult
import api.data.getUserEmail
import api.data.userDataState
import api.utils.getErrorTitleAndMessage
import com.notes.auth_ui.data.DialogState
import com.notes.auth_ui.data.LoginUIState
import com.notes.auth_ui.data.RegisterUIState
import com.notes.auth_ui.data.UIState
import com.notes.auth_ui.data.VerificationUIState
import com.notes.auth_ui.data.copyLoginUIState
import com.notes.ui.Access
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

class AuthViewModel(
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = scopeOverride ?: viewModelScope
    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState = _dialogState.asStateFlow()

    private val interactor = Interactor()

    fun login(
        state: LoginUIState,
        onSuccess: () -> Unit,
        context: Any?,
        args: Access
    ) {
        Platform().logger.logi("$TAG::login()")
        scope.launch {
            // Show progress
            val newState = _uiState.copyLoginUIState(showProgress = true)
            _uiState.emit(newState!!)

            val result = interactor.login(state.password, state.email, context)
            if (result.isSuccess()) {
                Platform().logger.logi("$TAG::login() reauth success")
                if (args.showChangePasswordUI) {
                    Platform().logger.logi("$TAG::login() showing change password screen")
                    // User password is confirmed. Show change password screen
                    onShowRegisterUI(args)
                } else {
                    onSuccess()
                }
            } else {
                if (state.authToConfirm) {
                    Platform().logger.loge("$TAG::login() reauth failed")
                }
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
        onSuccess: suspend () -> Unit
    ) {
        Platform().logger.logi("$TAG::register()")
        scope.launch {
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

    fun changePassword(
        state: RegisterUIState,
        onSuccess: () -> Unit
    ) {
        Platform().logger.logi("$TAG::changePassword()")
        scope.launch {

            if (state.email != interactor.getEmail()) {
                // Show error
                showDialog(
                    title = "Error",
                    subtitle = "Entered email is not correct",
                    onConfirm = {
                        dismissDialog()
                    }
                )
                return@launch
            }

            if (state.confirmPassword != state.password) {
                // Show error
                showDialog(
                    title = "Error",
                    subtitle = "Passwords do not match",
                    onConfirm = {
                        dismissDialog()
                    }
                )
                return@launch
            }

            val result = interactor.changePassword(state.password)

            if (result) {

                showDialog(
                    title = "Success",
                    subtitle = "Password has changed successfully!",
                    onConfirm = {
                        onSuccess()
                    }
                )
            } else {
                showDialog(
                    title = "Error",
                    subtitle = "Failed to change password. Please, try again!",
                    onConfirm = {
                        dismissDialog()
                    }
                )
            }

        }
    }

    fun runConfirmationCheck(
        shouldResendVerification: Boolean = false,
        navController: NavController,
    ) {
        Platform().logger.logi("$TAG::runConfirmationCheck()")
        scope.launch {

            if (userDataState.value.code.isNotEmpty()) {
                interactor.verifyCode(userDataState.value.code)
            }

            val isEmailVerified = interactor.isEmailVerified()
            if (isEmailVerified) {
                Platform().logger.logi("$TAG::runConfirmationCheck() passed")
                onShowLoginUI(args = Access())
                // Open login screen
                navController.navigate(Access())
            } else if (shouldResendVerification) {
                Platform().logger.logi("$TAG::runConfirmationCheck() resending verification")
                val result = interactor.sendEmailVerification()
                handleResult(result)
            } else {
                Platform().logger.logi("$TAG::runConfirmationCheck() no-op")
            }
        }
    }

    fun onShowAccessUI(args: Access) {
        scope.launch {
            // Special case to handle if user don't want to register
            // and to allow he/she to go strait to login ui
            // also in some cases we should auth user again
            val loginUIForced = args.forceLoginUI ||
                    (_uiState.value is LoginUIState && (_uiState.value as LoginUIState).uiForced) ||
                    args.showChangePasswordUI

            if (loginUIForced) {
                onShowLoginUI(args)
            } else if (getUserEmail().isEmpty()) {
                onShowRegisterUI(args)
            } else {
                onShowLoginUI(args)
            }
        }
    }

    fun onShowOnBoardingUI(navController: NavController) {
        // Check if can proceed
        runConfirmationCheck(navController = navController)
    }

    private suspend fun onShowLoginUI(args: Access) {
        // Initially we are going to show a keyboard if ui is open
        val newState = LoginUIState(
            hasFocus = true,
            email = interactor.getEmail(),
            uiForced = args.forceLoginUI,
            authToConfirm = args.showChangePasswordUI
        )
        _uiState.emit(newState)
    }

    private suspend fun onShowVerificationUI() {
        updateVerificationUIState(email = interactor.getEmail(), force = true)
    }

    private suspend fun onShowRegisterUI(args: Access) {
        // Initially we are going to show a keyboard if ui is open
        _uiState.emit(
            RegisterUIState(
                hasFocus = true,
                showChangePassword = args.showChangePasswordUI
            )
        )
    }

    fun dismissDialog() {
        Platform().logger.logi("$TAG::dismissDialog()")
        scope.launch {
            _dialogState.emit(null)
        }
    }

    private suspend fun showDialog(
        title: String,
        subtitle: String,
        onConfirm: (() -> Unit)? = null
    ) {
        _dialogState.emit(DialogState(title = title, subtitle = subtitle, onConfirm = onConfirm))
    }

    override fun onCleared() {
        Platform().logger.logi("$TAG::onCleared()")
        interactor.onClear()
    }

    private suspend fun updateVerificationUIState(
        emailVerificationSent: Boolean? = null,
        email: String? = null,
        force: Boolean = false
    ) {
        val copy = if (force) VerificationUIState() else _uiState.value as? VerificationUIState ?: return
        _uiState.emit(
            VerificationUIState(
                emailVerificationSent = emailVerificationSent ?: copy.emailVerificationSent,
                email = email ?: copy.email,
            )
        )
    }

    private suspend fun handleResult(result: AuthResult) {
        Platform().logger.logi("$TAG::handleResult()")

        if (result.isEmailVerificationPassed()) {
            updateVerificationUIState(emailVerificationSent = true)
            return
        }

        if (result.isEmailVerificationFailed()) {
            updateVerificationUIState(emailVerificationSent = false)
            return
        }

        // Handle other errors
        val strings = getErrorTitleAndMessage(result)
        val title = strings.first
        val subtitle = strings.second

        showDialog(title = title, subtitle = subtitle)
    }
}
