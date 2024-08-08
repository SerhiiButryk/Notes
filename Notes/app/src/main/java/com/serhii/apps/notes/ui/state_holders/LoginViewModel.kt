/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.state_holders

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.EventService
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.types.UIRequestType
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.apps.notes.ui.dialogs.base.AlertDialogHelper
import com.serhii.core.log.Log
import com.serhii.core.security.BiometricAuthenticator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.crypto.Cipher

/**
 *  View model for managing UI state and business logic of Authorization Activity
 */

private const val TAG = "LoginViewModel"

class LoginViewModel : ViewModel() {

    // UI State declarations

    @Stable
    class DialogUIState(
        val title: Int = -1,
        val message: Int = -1,
        val onConfirm: () -> Unit = {},
        val onCancel: () -> Unit = {}
    )

    @Stable
    open class AuthUIState(
        val title: String = "",
        val emailFiledLabel: String = "",
        val emailFiledHint: String = "",
        val passwordFiledLabel: String = "",
        val passwordFiledHint: String = "",
        val buttonText: String = "",
        val descriptionText: String = "",
        val requestType: UIRequestType = UIRequestType.UN_SET,
        var openDialog: Boolean = false,
        var dialogState: DialogUIState = DialogUIState()
    )

    @Stable
    class LoginUIState(
        title: String,
        emailFiledLabel: String,
        emailFiledHint: String,
        passwordFiledLabel: String,
        passwordFiledHint: String,
        buttonText: String,
        val hasBiometric: Boolean,
        val biometricButtonText: String,
        uiRequestType: UIRequestType
    ) : AuthUIState(
        title = title,
        emailFiledLabel = emailFiledLabel,
        emailFiledHint = emailFiledHint,
        passwordFiledLabel = passwordFiledLabel,
        passwordFiledHint = passwordFiledHint,
        buttonText = buttonText,
        requestType = uiRequestType
    )

    @Stable
    class RegistrationUIState(
        title: String,
        emailFiledLabel: String,
        emailFiledHint: String,
        passwordFiledLabel: String,
        passwordFiledHint: String,
        buttonText: String,
        val confirmPasswordFiledLabel: String,
        val confirmPasswordFiledHint: String,
        uiRequestType: UIRequestType
    ) : AuthUIState(
        title = title,
        emailFiledLabel = emailFiledLabel,
        emailFiledHint = emailFiledHint,
        passwordFiledLabel = passwordFiledLabel,
        passwordFiledHint = passwordFiledHint,
        buttonText = buttonText,
        requestType = uiRequestType
    )

    @Stable
    class WelcomeUIState(
        title: String,
        descriptionText: String,
        buttonText: String,
        uiRequestType: UIRequestType
    ) : AuthUIState(
        title = title,
        descriptionText = descriptionText,
        buttonText = buttonText,
        requestType = uiRequestType
    )

    // UI State of the managed auth screens
    private val authStateFlow: MutableStateFlow<AuthUIState> = MutableStateFlow(AuthUIState())
    val uiState: StateFlow<AuthUIState> = authStateFlow

    // Current user auth data
    val authModel: AuthModel = AuthModel()
    private var biometricAuthManager: BiometricAuthenticator? = null

    fun initViewModel(context: Context) {
        Log.info(TAG, "initViewModel()")
        authStateFlow.value = getUIState(context)

        if (BiometricAuthenticator.biometricsAvailable(context) && biometricAuthManager == null) {
            biometricAuthManager = BiometricAuthenticator()
        }
    }

    private fun getUIState(context: Context): AuthUIState {
        Log.info(TAG, "getUIState()")

        val userExists = NativeBridge.userName.isNotEmpty()

        authModel.email = NativeBridge.userName

        return if (userExists) {
            Log.info(TAG, "Open Login UI")
            // We have a registered user so show login ui
            createLoginUIState(context)
        } else {
            Log.info(TAG, "Open Welcome UI")
            // Show welcome ui
            createWelcomeUIState(context)
        }
    }

    fun proceed(
        requestType: UIRequestType,
        context: Context,
        authModel: AuthModel = AuthModel(),
        type: Int = -1
    ) {
        Log.info(TAG, "proceed()")

        viewModelScope.launch(App.BACKGROUND_DISPATCHER) {

            Log.detail(TAG, "proceed() >>")

            when (requestType) {

                UIRequestType.WELCOME_UI -> {
                    // Open next screen
                    authStateFlow.value = createRegistrationUIState(context)
                }

                UIRequestType.REGISTRATION -> {

                    val showBiometricDialog = suspend {
                        withContext(App.UI_DISPATCHER) {
                            requestBiometricDialog(context, createRegistrationUIState(context))
                        }
                    }

                    EventService.onRegistration(
                        authModel,
                        BiometricAuthenticator.biometricsAvailable(context),
                        showBiometricDialog
                    )
                }

                UIRequestType.PASSWORD_LOGIN -> {
                    EventService.onPasswordLogin(context, authModel)
                }

                UIRequestType.UNLOCK -> {}
                UIRequestType.BIOMETRIC_LOGIN -> {
                    EventService.onBiometricLogin(authModel)
                }

                UIRequestType.UN_SET -> {}

                UIRequestType.SHOW_DIALOG -> {

                    // TODO: Doest look good, might be improved
                    val newUiState: AuthUIState = if (uiState.value is LoginUIState) {
                        createLoginUIState(context)
                    } else {
                        createRegistrationUIState(context)
                    }

                    requestDialog(
                        AlertDialogHelper.getTitleFor(type),
                        AlertDialogHelper.getMessageFor(type),
                        { /* No-op */ },
                        { /* No-op */ },
                        newUiState
                    )
                }

                UIRequestType.BLOCK_UI -> TODO()

                UIRequestType.LOGIN_UI -> {
                    clearSensitiveData()
                    // Open next screen
                    authStateFlow.value = createLoginUIState(context)
                }
            }

            clearSensitiveData()

            Log.detail(TAG, "proceed() <")
        }
    }

    private fun clearSensitiveData() {
        authModel.password = ""
        authModel.confirmPassword = ""
    }

    private fun requestBiometricDialog(context: Context, uiState: AuthUIState) {
        Log.detail(TAG, "requestBiometricDialog()")

        // Start authentication using biometrics
        biometricAuthManager?.authenticateInitial(object : BiometricAuthenticator.Listener {

            override fun onSuccess(cipher: Cipher) {
                Log.detail(TAG, "BiometricAuthenticator.Listener: onSuccess()")

                EventService.onRegistrationDone(
                    completedSuccessfully = true,
                    authModel = authModel,
                    cipher = cipher
                )
            }

            override fun onFailure() {
                Log.error(TAG, "BiometricAuthenticator.Listener: onFailure()")

                requestDialog(
                    title = R.string.biometric_dialog_title,
                    message = R.string.biometric_dialog_message,
                    onConfirm = {
                        Log.info(TAG, "BiometricAuthenticator.Listener: re-open")
                        requestBiometricDialog(context, createRegistrationUIState(context))
                    },
                    onCancel = {
                        Log.info(TAG, "BiometricAuthenticator.Listener: cancel biometric")
                        EventService.onRegistrationDone(
                            completedSuccessfully = false,
                            authModel = authModel
                        )
                    },
                    uiState
                )
            }

        })
    }

    private fun requestDialog(
        title: Int,
        message: Int,
        onConfirm: () -> Unit,
        onCancel: () -> Unit,
        uiState: AuthUIState
    ) {

        uiState.dialogState = DialogUIState(
            title = title,
            message = message,
            onConfirm = onConfirm,
            onCancel = onCancel
        )

        uiState.openDialog = true

        authStateFlow.value = uiState
    }

    private fun createLoginUIState(context: Context): LoginUIState {
        return LoginUIState(
            title = context.getString(R.string.title_login),
            emailFiledLabel = context.getString(R.string.usr_email),
            emailFiledHint = "Type email",
            passwordFiledLabel = context.getString(R.string.usr_psw),
            passwordFiledHint = "Type password",
            buttonText = context.getString(R.string.login_btn),
            hasBiometric = BiometricAuthenticator.biometricsAvailable(context),
            biometricButtonText = context.getString(R.string.login_with_biometric),
            UIRequestType.PASSWORD_LOGIN
        )
    }

    private fun createRegistrationUIState(context: Context): RegistrationUIState {
        return RegistrationUIState(
            title = context.getString(R.string.title_reg),
            emailFiledLabel = context.getString(R.string.usr_email),
            emailFiledHint = "Type email",
            passwordFiledLabel = context.getString(R.string.usr_psw),
            passwordFiledHint = "Type password",
            buttonText = context.getString(R.string.btn_continue),
            confirmPasswordFiledLabel = context.getString(R.string.usr_approve_new_psw),
            confirmPasswordFiledHint = "Type password",
            UIRequestType.REGISTRATION
        )
    }

    private fun createWelcomeUIState(context: Context): WelcomeUIState {
        return WelcomeUIState(
            title = context.getString(R.string.title_login),
            descriptionText = context.getString(R.string.text_description),
            buttonText = context.getString(R.string.btn_continue),
            uiRequestType = UIRequestType.WELCOME_UI
        )
    }
}