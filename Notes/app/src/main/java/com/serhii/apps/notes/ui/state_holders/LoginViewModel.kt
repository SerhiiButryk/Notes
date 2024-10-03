/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.state_holders

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.EventService
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.types.UIRequestType
import com.serhii.apps.notes.ui.DialogHelper
import com.serhii.apps.notes.ui.DialogUIState
import com.serhii.apps.notes.ui.data_model.AuthModel
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

class LoginViewModel : AppViewModel() {

    // UI State of the managed auth screens
    private val _uiState: MutableStateFlow<BaseUIState> = MutableStateFlow(BaseUIState())
    val uiState: StateFlow<BaseUIState> = _uiState

    // Current user auth data
    val authModel: AuthModel = AuthModel()
    private var biometricAuthManager: BiometricAuthenticator? = null

    override fun initViewModel(activity: Activity) {
        Log.info(TAG, "initViewModel()")
        super.initViewModel(activity)
        _uiState.value = getUIState(activity.applicationContext)

        if (BiometricAuthenticator.biometricsAvailable(activity.applicationContext) && biometricAuthManager == null) {
            biometricAuthManager = BiometricAuthenticator()
        }
    }

    fun setupBiometrics(activity: FragmentActivity) {
        if (BiometricAuthenticator.biometricsAvailable(activity.applicationContext)) {
            biometricAuthManager?.init(
                activity.applicationContext,
                activity,
                activity.getString(R.string.biometric_prompt_title),
                activity.getString(R.string.biometric_prompt_subtitle),
                activity.getString(android.R.string.cancel)
            )
        }
    }

    private fun getUIState(context: Context): BaseUIState {
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

            val showBiometrics = suspend {
                withContext(App.UI_DISPATCHER) {
                    requestBiometricDialog(context, createRegistrationUIState(context))
                }
            }

            when (requestType) {

                UIRequestType.WELCOME_UI -> {
                    // Open next screen
                    _uiState.value = createRegistrationUIState(context)
                }

                UIRequestType.REGISTRATION -> {
                    EventService.onRegistration(
                        authModel,
                        BiometricAuthenticator.biometricsAvailable(context),
                        showBiometrics
                    )
                }

                UIRequestType.PASSWORD_LOGIN -> {
                    EventService.onPasswordLogin(context, authModel)
                }

                UIRequestType.UNLOCK -> {}
                UIRequestType.BIOMETRIC_LOGIN -> {
                    EventService.onBiometricLogin(authModel) {
                        withContext(App.UI_DISPATCHER) {
                            showMessage(context, R.string.biometric_toast_message)
                        }
                    }
                }

                UIRequestType.UN_SET -> {}

                UIRequestType.DIALOG_UI -> {

                    val createNewUiState = {
                        // TODO: Doest not look good, might be revisited later
                        if (uiState.value is LoginUIState) {
                            createLoginUIState(context)
                        } else {
                            createRegistrationUIState(context)
                        }
                    }

                    openDialog(DialogHelper.getTitleFor(type), DialogHelper.getMessageFor(type), {
                        // TODO: Doest not look good, might be revisited later
                        // A hack to close dialog
                        _uiState.value = createNewUiState()
                    }, {
                        // TODO: Doest not look good, might be revisited later
                        // A hack to close dialog
                        _uiState.value = createNewUiState()
                    }, uiState = createNewUiState()
                    )
                }

                UIRequestType.BLOCK_UI -> {}

                UIRequestType.LOGIN_UI -> {
                    // Open next screen
                    _uiState.value = createLoginUIState(context)
                }

                UIRequestType.BIOMETRICS_UI -> {
                    showBiometrics()
                }

                UIRequestType.FORGOT_PASSWORD_UI -> {
                    // Open next screen
                    _uiState.value = createForgotPasswordUIState(context)
                }
            }

            Log.detail(TAG, "proceed() <<")
        }
    }

    private fun requestBiometricDialog(context: Context, uiState: BaseUIState) {
        Log.detail(TAG, "requestBiometricDialog()")

        // Start authentication using biometrics
        biometricAuthManager?.authenticateInitial(object : BiometricAuthenticator.Listener {

            override fun onSuccess(cipher: Cipher) {
                Log.detail(TAG, "BiometricAuthenticator.Listener: onSuccess()")

                EventService.onRegistrationDone(
                    completedSuccessfully = true, authModel = authModel, cipher = cipher
                )
            }

            override fun onFailure() {
                Log.error(TAG, "BiometricAuthenticator.Listener: onFailure()")

                openDialog(
                    title = R.string.biometric_dialog_title,
                    message = R.string.biometric_dialog_message,
                    onConfirm = {
                        Log.info(TAG, "BiometricAuthenticator.Listener: re-open")
                        proceed(UIRequestType.BIOMETRICS_UI, context)
                    },
                    onCancel = {
                        Log.info(TAG, "BiometricAuthenticator.Listener: cancel biometric")
                        EventService.onRegistrationDone(
                            completedSuccessfully = false, authModel = authModel
                        )
                    },
                    uiState = uiState,
                    positiveBtn = R.string.yes,
                    negativeBtn = R.string.no,
                    hasCancelButton = true,
                )
            }

        })
    }

    private fun openDialog(
        title: Int,
        message: Int,
        onConfirm: () -> Unit,
        onCancel: () -> Unit,
        positiveBtn: Int = android.R.string.ok,
        negativeBtn: Int = android.R.string.cancel,
        hasCancelButton: Boolean = false,
        uiState: BaseUIState
    ) {

        uiState.dialogState = requestDialog(
            title = title,
            message = message,
            onConfirm = onConfirm,
            onCancel = onCancel,
            hasCancelButton = hasCancelButton,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn
        )

        uiState.openDialog = true

        _uiState.value = uiState
    }

    fun navigateBack(context: Context): Boolean {
        val currentUIState = _uiState.value

        if (currentUIState is ForgotPasswordUIState) {
            // Go to previous screen
            _uiState.value = createLoginUIState(context)
            return true
        }

        return false
    }

    // UI State factory methods

    private fun createLoginUIState(context: Context): LoginUIState {
        return LoginUIState(
            title = context.getString(R.string.title_login),
            emailFiledLabel = context.getString(R.string.usr_email),
            emailFiledHint = context.getString(R.string.type_email),
            passwordFiledLabel = context.getString(R.string.usr_psw),
            passwordFiledHint = context.getString(R.string.type_password),
            buttonText = context.getString(R.string.login_btn),
            hasBiometric = BiometricAuthenticator.biometricsAvailable(context) && BiometricAuthenticator.isReady(),
            biometricButtonText = context.getString(R.string.login_with_biometric),
            UIRequestType.PASSWORD_LOGIN
        )
    }

    private fun createRegistrationUIState(context: Context): RegistrationUIState {
        return RegistrationUIState(
            title = context.getString(R.string.title_reg),
            emailFiledLabel = context.getString(R.string.usr_email),
            emailFiledHint = context.getString(R.string.type_email),
            passwordFiledLabel = context.getString(R.string.usr_psw),
            passwordFiledHint = context.getString(R.string.type_password),
            buttonText = context.getString(R.string.btn_continue),
            confirmPasswordFiledLabel = context.getString(R.string.usr_approve_new_psw),
            confirmPasswordFiledHint = context.getString(R.string.type_password),
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

    private fun createForgotPasswordUIState(context: Context): ForgotPasswordUIState {
        return ForgotPasswordUIState(
            title = context.getString(R.string.forgot_password_title),
            passwordFiledLabel = context.getString(R.string.usr_psw),
            passwordFiledHint = context.getString(R.string.type_password),
            buttonText = context.getString(R.string.btn_continue),
            confirmPasswordFiledLabel = context.getString(R.string.usr_approve_new_psw),
            confirmPasswordFiledHint = context.getString(R.string.type_password),
            uiRequestType = UIRequestType.FORGOT_PASSWORD_UI
        )
    }

    @Stable
    open class BaseUIState(
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
    ) : BaseUIState(
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
    ) : BaseUIState(
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
        title: String, descriptionText: String, buttonText: String, uiRequestType: UIRequestType
    ) : BaseUIState(
        title = title,
        descriptionText = descriptionText,
        buttonText = buttonText,
        requestType = uiRequestType
    )

    @Stable
    class ForgotPasswordUIState(
        title: String, buttonText: String, uiRequestType: UIRequestType,
        val confirmPasswordFiledLabel: String,
        val confirmPasswordFiledHint: String,
        passwordFiledLabel: String,
        passwordFiledHint: String,
        var password: String = "",
        var confirmPassword: String = ""
    ) : BaseUIState(
        title = title,
        buttonText = buttonText,
        requestType = uiRequestType,
        passwordFiledHint = passwordFiledHint,
        passwordFiledLabel = passwordFiledLabel
    )
}