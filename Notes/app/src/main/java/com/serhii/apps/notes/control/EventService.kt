/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control

import android.content.Context
import com.serhii.apps.notes.control.auth.AuthManager
import com.serhii.apps.notes.control.auth.RequestType
import com.serhii.apps.notes.control.auth.base.IEventService
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.core.log.Log
import com.serhii.core.security.Crypto
import javax.crypto.Cipher

/**
 * Class which receives the events from UI layer and delivers them to appropriate party.
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
    override suspend fun onRegistration(
        model: AuthModel,
        hasBiometric: Boolean,
        requestBiometricDialog: suspend () -> Unit
    ) {
        // Initiate registration request
        val result = authManager.checkInput(model.password, model.confirmPassword, model.email)
        if (result) {
            Log.info(TAG, "onRegistration() initial request processed")

            // Checks are passed and user is registered.
            // So, we should create application keys and ask for biometric login here.
            val keyMaster = crypto.getKeyMaster()
            keyMaster.createKey(model.password)

            if (!hasBiometric) {
                // Finish
                authManager.handleRequest(RequestType.REQ_REGISTER, model)
                // For safety
                model.confirmPassword = ""
                model.password = ""
                return
            } else {
                requestBiometricDialog()
            }
        } else {
            Log.error(TAG, "onRegistration() initial request failed")
            // For safety
            model.confirmPassword = ""
            model.password = ""
        }
    }

    /**
     * Handle registration done event
     */
    override fun onRegistrationDone(
        completedSuccessfully: Boolean,
        cipher: Cipher?,
        authModel: AuthModel
    ) {
        if (completedSuccessfully) {
            Log.detail(TAG, "onRegistrationDone() result is success")
            val keyMaster = crypto.getKeyMaster()
            keyMaster.createKey(cipher!!)
        } else {
            Log.info(TAG, "onRegistrationDone() failure or canceled")
        }
        // Finish
        authManager.handleRequest(RequestType.REQ_REGISTER, authModel)
        // For safety
        authModel.confirmPassword = ""
        authModel.password = ""
    }

    /**
     * Handle biometric login event
     */
    override suspend fun onBiometricLogin(authModel: AuthModel, requestMessage: suspend () -> Unit) {
        Log.info(TAG, "onBiometricLogin()")

        // Safe check. Should not happen in a normal case.
        if (authModel.cipher == null) {
            Log.error(TAG, "onBiometricLogin(), cipher is null")
            requestMessage()
            return
        }

        // Safe check. Should not happen in a normal case.
        val success = crypto.getKeyMaster().initKeys(authModel.cipher!!)
        if (!success) {
            Log.error(TAG, "onBiometricLogin(), failed to init keys")
            requestMessage()
            return
        }

        // Complete the authentication
        authManager.handleRequest(RequestType.REQ_BIOMETRIC_LOGIN, authModel)

        // For safety
        authModel.confirmPassword = ""
        authModel.password = ""
    }

    /**
     * Handle registration done event
     */
    override fun onRegistrationDone(context: Context) {
        Log.info(TAG, "onRegistrationDone()")
        crypto.getKeyMaster().createUnlockKey()
        NativeBridge.resetLoginLimitLeft(context)
    }

    /**
     * Handle password change event
     */
    override fun onChangePassword(
        oldPassword: String,
        newPassword: String,
        showMessage: (id: Int) -> Unit
    ): Boolean {
        var result = false
// TODO: Handle this
//        val success = NativeBridge.verifyPassword(Hash.hashMD5(oldPassword))
//        if (!success) {
//            showMessage(R.string.change_password_toast_not_correct_password)
//        } else {
//            result = NativeBridge.setNewPassword(Hash.hashMD5(newPassword))
//            if (result) {
//                showMessage(R.string.change_password_toast_password_set)
//            } else {
//                showMessage(R.string.change_password_toast_password_error)
//            }
//        }
        return result
    }

    /**
     * Handle login limit change event
     */
    override fun onChangeLoginLimit(newLimit: Int) {
        NativeBridge.unlockLimit = newLimit
    }

    /**
     * Handle error event
     */
    override fun onErrorState() {
        Log.info(TAG, "onErrorState()")
    }

    private const val TAG = "EventService"
}