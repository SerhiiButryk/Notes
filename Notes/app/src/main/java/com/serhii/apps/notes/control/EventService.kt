/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control

import android.content.Context
import com.serhii.apps.notes.control.auth.base.IAuthorizeService
import com.serhii.apps.notes.control.auth.AuthManager
import com.serhii.apps.notes.control.auth.types.RequestType
import com.serhii.apps.notes.ui.data_model.AuthCredsModel
import com.serhii.core.log.Log.Companion.info

/**
 * Class which receives the events from other classes and delivers them to appropriate party.
 */
object EventService : IAuthorizeService {

    /**
     * Handle authorize event
     */
    override fun onPasswordLogin(model: AuthCredsModel) {
        val authManager = AuthManager()
        // Initiate authorize request
        authManager.handleRequest(RequestType.REQ_AUTHORIZE, model)
    }

    /**
     * Handle registration event
     */
    override fun onRegistration(model: AuthCredsModel) {
        val authManager = AuthManager()
        // Initiate authorize request
        authManager.handleRequest(RequestType.REQ_REGISTER, model)
    }

    /**
     * Handle biometric login event
     */
    override fun onBiometricLogin() {
        val authManager = AuthManager()
        // Initiate authorize request
        authManager.handleRequest(RequestType.REQ_BIOMETRIC_LOGIN, AuthCredsModel())
    }

    /**
     * Handle user registered event
     * User account is created, do one time application setup which is needed in this case
     */
    override fun onUserRegistered(context: Context) {
        info(TAG, "onUserRegistered()")
        // Here we creates app session keys for user
        // and set other settings which are related to user's security
        val nativeBridge = NativeBridge()
        nativeBridge.createUnlockKey()
        // Set default login password limit
        nativeBridge.setLoginLimitFromDefault(context)
    }

    private const val TAG = "EventService"
}