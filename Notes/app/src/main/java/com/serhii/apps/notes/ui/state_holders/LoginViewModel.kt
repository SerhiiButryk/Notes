/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.state_holders

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Stable
import androidx.compose.ui.focus.FocusRequester
import androidx.fragment.app.FragmentActivity
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
import com.serhii.core.utils.GoodUtils
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

    // UI State of the managed auth screens
    private val authStateFlow: MutableStateFlow<AuthUIState> = MutableStateFlow(AuthUIState())
    val uiState: StateFlow<AuthUIState> = authStateFlow

    // Current user auth data
    val authModel: AuthModel = AuthModel()
    private var biometricAuthManager: BiometricAuthenticator? = null

    private var immManager: InputMethodManager? = null

    fun initViewModel(context: Context) {
        Log.info(TAG, "initViewModel()")
        authStateFlow.value = getUIState(context)

        if (BiometricAuthenticator.biometricsAvailable(context) && biometricAuthManager == null) {
            biometricAuthManager = BiometricAuthenticator()
        }

        immManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    fun setupBiometrics(activity: FragmentActivity) {
        if (BiometricAuthenticator.biometricsAvailable(activity.applicationContext)) {
            biometricAuthManager?.init(activity.applicationContext, activity,
                activity.getString(R.string.biometric_prompt_title),
                activity.getString(R.string.biometric_prompt_subtitle),
                activity.getString(android.R.string.cancel))
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

            val showBiometrics = suspend {
                withContext(App.UI_DISPATCHER) {
                    requestBiometricDialog(context, createRegistrationUIState(context))
                }
            }

            when (requestType) {

                UIRequestType.WELCOME_UI -> {
                    // Open next screen
                    authStateFlow.value = createRegistrationUIState(context)
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
                            GoodUtils.showToast(context, R.string.biometric_toast_message)
                        }
                    }
                }

                UIRequestType.UN_SET -> {}

                UIRequestType.SHOW_DIALOG -> {

                    // TODO: Doest look good, might be revisited later
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
                        uiState = newUiState
                    )
                }

                UIRequestType.BLOCK_UI -> {}

                UIRequestType.LOGIN_UI -> {
                    // Open next screen
                    authStateFlow.value = createLoginUIState(context)
                }

                UIRequestType.SHOW_BIOMETRICS_UI -> {
                    showBiometrics()
                }

            }

            Log.detail(TAG, "proceed() <<")
        }
    }

    fun requestKeyboard(focusRequester: FocusRequester) {
        viewModelScope.launch(App.UI_DISPATCHER) {
            Log.detail(TAG, "requestKeyboard() try to show keyboard")
            if (immManager != null && !immManager!!.isAcceptingText()) {
                focusRequester.requestFocus()
                Log.detail(TAG, "requestKeyboard() done")
            }
        }
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
                        proceed(UIRequestType.SHOW_BIOMETRICS_UI, context)
                    },
                    onCancel = {
                        Log.info(TAG, "BiometricAuthenticator.Listener: cancel biometric")
                        EventService.onRegistrationDone(
                            completedSuccessfully = false,
                            authModel = authModel
                        )
                    },
                    uiState = uiState,
                    positiveBtn = R.string.yes,
                    negativeBtn = R.string.no
                )
            }

        })
    }

    private fun requestDialog(
        title: Int,
        message: Int,
        onConfirm: () -> Unit,
        onCancel: () -> Unit,
        positiveBtn: Int = android.R.string.ok,
        negativeBtn: Int = android.R.string.cancel,
        uiState: AuthUIState
    ) {

        uiState.dialogState = DialogUIState(
            title = title,
            message = message,
            onConfirm = onConfirm,
            onCancel = onCancel,
            hasCancelButton = true,
            dialogDismissible = false,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn
        )

        uiState.openDialog = true

        authStateFlow.value = uiState
    }

    // UI State factory methods

    private fun createLoginUIState(context: Context): LoginUIState {
        return LoginUIState(
            title = context.getString(R.string.title_login),
            emailFiledLabel = context.getString(R.string.usr_email),
            emailFiledHint = "Type email",
            passwordFiledLabel = context.getString(R.string.usr_psw),
            passwordFiledHint = "Type password",
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

    // UI State declarations

    @Stable
    class DialogUIState(
        val title: Int = -1,
        val message: Int = -1,
        val dialogDismissible: Boolean = true,
        val hasCancelButton: Boolean = false,
        val onConfirm: () -> Unit = {},
        val onCancel: () -> Unit = {},
        val positiveBtn: Int = android.R.string.ok,
        val negativeBtn: Int = android.R.string.cancel,
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
}