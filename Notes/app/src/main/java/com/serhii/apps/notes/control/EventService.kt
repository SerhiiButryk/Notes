/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.auth.AuthManager
import com.serhii.apps.notes.control.auth.base.IEventService
import com.serhii.apps.notes.control.auth.types.AuthResult
import com.serhii.apps.notes.control.auth.types.RequestType
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.apps.notes.ui.dialogs.ConfirmDialogCallback
import com.serhii.apps.notes.ui.dialogs.DialogHelper
import com.serhii.core.log.Log
import com.serhii.core.security.BiometricAuthenticator
import com.serhii.core.security.Crypto
import com.serhii.core.security.Hash
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.crypto.Cipher

/**
 * Class which receives the events from other classes and delivers them to appropriate party.
 */
object EventService : IEventService {

    private val authManager = AuthManager()
    private val crypto = Crypto()

    /**
     * Handle authorize event
     */
    override suspend fun onPasswordLogin(context: Context, model: AuthModel) {
        Log.info(TAG, "onPasswordLogin()")
        // Do password check and any other necessary actions
        if (authManager.handleRequest(RequestType.REQ_AUTHORIZE, model)) {

            crypto.getKeyMaster().initKeys(model.password)

            NativeBridge.resetLoginLimitLeft(context)

            // Auth is passed after this step

            // Complete the authentication
            authManager.complete()
        } else {
            Log.error(TAG, "onPasswordLogin() not authorized")
        }
    }

    /**
     * Handle registration event
     */
    override suspend fun onRegistration(model: AuthModel,
                                        biometricAuthenticator: BiometricAuthenticator?,
                                        fragmentActivity: FragmentActivity,
                                        coroutineScope: CoroutineScope) {
        // Initiate registration request
        val result = authManager.checkInput(model.password, model.confirmPassword, model.email)
        if (result) {
            Log.info(TAG, "onRegistration() success")

            // Checks are passed and user is registered.
            // So, we should create application keys and ask for biometric login here.
            val keyMaster = crypto.getKeyMaster()
            keyMaster.createKey(model.password)

            if (biometricAuthenticator == null) {
                // Finish
                authManager.handleRequest(RequestType.REQ_REGISTER, model)
                // For safety
                model.confirmPassword = ""
                model.password = ""
                return
            }

            // Show biometrics
            withContext(App.UI_DISPATCHER) {
                // If not null then can ask for Biometric login
                biometricAuthenticator.authenticateInitial(object : BiometricAuthenticator.Listener {

                    override fun onSuccess(cipher: Cipher) {
                        Log.detail(TAG, "BiometricAuthenticator onSuccess()")
                        keyMaster.createKey(cipher)
                        // Finish
                        authManager.handleRequest(RequestType.REQ_REGISTER, model)
                        // For safety
                        model.confirmPassword = ""
                        model.password = ""
                    }

                    override fun onFailure() {
                        Log.error(TAG, "BiometricAuthenticator onFailure()")

                        DialogHelper.showConfirmDialog(fragmentActivity, object : ConfirmDialogCallback {

                            override fun onOk() {
                                coroutineScope.launch(App.BACKGROUND_DISPATCHER
                                        + CoroutineName("RegistrationRetry")) {
                                    Log.info(TAG, "BiometricAuthenticator Retrying...")
                                    onRegistration(model, biometricAuthenticator, fragmentActivity, this)
                                }
                            }

                            override fun onCancel() {
                                Log.info(TAG, "BiometricAuthenticator Finishing...")
                                // Finish
                                authManager.handleRequest(RequestType.REQ_REGISTER, model)
                                // For safety
                                model.confirmPassword = ""
                                model.password = ""
                            }
                        }, R.string.biometric_dialog_title, R.string.biometric_dialog_message)
                    }
                })
            }
        } else {
            Log.error(TAG, "onRegistration() failure")
        }
    }

    /**
     * Handle biometric login event
     */
    override suspend fun onBiometricLogin(authModel: AuthModel, showMessage: (id: Int) -> Unit) {
        Log.info(TAG, "onBiometricLogin()")

        // Safe check. Should not happen in a normal case.
        if (authModel.cipher == null) {
            Log.error(TAG, "onBiometricLogin(), cipher is null")
            withContext(App.UI_DISPATCHER) {
                showMessage(R.string.biometric_toast_message)
            }
            return
        }

        val success = crypto.getKeyMaster().initKeys(authModel.cipher!!)
        if (!success) {
            Log.error(TAG, "onBiometricLogin(), filed to init keys")
            withContext(App.UI_DISPATCHER) {
                showMessage(R.string.biometric_toast_message)
            }
            return
        }

        // Complete the authentication
        authManager.handleRequest(RequestType.REQ_BIOMETRIC_LOGIN, authModel)
    }

    /**
     * Handle registration finished event
     */
    override fun onRegistrationDone(context: Context, coroutineScope: CoroutineScope) {
        Log.info(TAG, "onRegistrationDone()")
        coroutineScope.launch(App.BACKGROUND_DISPATCHER) {
            crypto.getKeyMaster().createUnlockKey()
            NativeBridge.resetLoginLimitLeft(context)
        }
    }

    /**
     * Handle change password event
     *
     * TODO: Improve to work with KeyMater
     */
    override fun onChangePassword(oldPassword: String, newPassword: String, showMessage: (id: Int) -> Unit): Boolean {
        var result = false
        val success = NativeBridge.verifyPassword(Hash.hashMD5(oldPassword))
        if (!success) {
            showMessage(R.string.change_password_toast_not_correct_password)
        } else {
            result = NativeBridge.setNewPassword(Hash.hashMD5(newPassword))
            if (result) {
                showMessage(R.string.change_password_toast_password_set)
            } else {
                showMessage(R.string.change_password_toast_password_error)
            }
        }
        return result
    }

    /**
     * Handle change change login limit event
     */
    override fun onChangeLoginLimit(newLimit: Int) {
        NativeBridge.unlockLimit = newLimit
    }

    /**
     * Handle error event
     */
    override fun onErrorState(type: Int, showDialog: () -> Unit) {
        Log.info(TAG, "onErrorState()")
        var shouldShowDialog = true
        if (type == AuthResult.WRONG_PASSWORD.typeId) {
            val currentLimit = NativeBridge.unlockLimit
            // If limit is exceeded then need to block application
            if (currentLimit == 1) {
                // Block application
                NativeBridge.executeBlockApp()
                // Block Ui is going to be shown. So do not show dialog.
                shouldShowDialog = false
            } else {
                // Update password limit value
                NativeBridge.unlockLimit -= 1
                Log.detail(TAG, "onErrorState(), updated limit")
            }
        }
        if (shouldShowDialog) {
            showDialog()
        }
    }

    private const val TAG = "EventService"
}