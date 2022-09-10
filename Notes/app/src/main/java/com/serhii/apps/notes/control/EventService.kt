/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control

import android.content.Context
import com.serhii.apps.notes.common.AppConstants
import com.serhii.apps.notes.control.base.IAuthorizeService
import com.serhii.apps.notes.control.managers.AuthManager
import com.serhii.apps.notes.control.types.RequestType
import com.serhii.apps.notes.database.Keys.SECRET_KEY_DATA_ENC_ALIAS
import com.serhii.apps.notes.database.Keys.SECRET_KEY_PASSWORD_ENC_ALIAS
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.Cipher

/**
 * Class which receives the events from the system and delivers them to corresponding manager
 *
 * See interface definition
 */
object EventService : IAuthorizeService {
    /**
     * Handle authorize event
     */
    override fun onPasswordLogin(model: AuthModel) {
        val authManager = AuthManager()
        // Initiate authorize request
        authManager.handleRequest(RequestType.REQ_AUTHORIZE, model)
    }

    /**
     * Handle registration event
     */
    override fun onRegistration(model: AuthModel) {
        val authManager = AuthManager()
        // Initiate authorize request
        authManager.handleRequest(RequestType.REQ_REGISTER, model)
    }

    /**
     * Handle finger print login event
     */
    override fun onBiometricLogin() {
        val authManager = AuthManager()
        // Initiate authorize request
        authManager.handleRequest(RequestType.REQ_BIOMETRIC_LOGIN, null)
    }

    /**
     * Handle user registered event
     *
     * User account is created, do one time application setup
     */
    override fun onUserRegistered(context: Context) {
        info(TAG, "onUserRegistered()")
        // Create protection keys
        val cipher = Cipher()
        cipher.createKey(SECRET_KEY_DATA_ENC_ALIAS, false)
        cipher.createKey(SECRET_KEY_PASSWORD_ENC_ALIAS, false)
        NativeBridge().createUnlockKey()
        // Set login password limit
        NativeBridge().setLoginLimitFromDefault(context)
    }

    private const val TAG = "EventService"
}