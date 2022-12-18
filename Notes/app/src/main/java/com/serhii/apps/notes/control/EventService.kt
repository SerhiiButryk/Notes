/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control

import android.content.Context
import com.serhii.apps.notes.control.auth.base.IAuthorizeService
import com.serhii.apps.notes.control.auth.AuthManager
import com.serhii.apps.notes.control.auth.types.RequestType
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.core.log.Log.Companion.info

/**
 * Class which receives the events from the system and delivers them to corresponding party.
 * See interface definition.
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
        val nativeBridge = NativeBridge()
        nativeBridge.createUnlockKey()
        // Set login password limit
        nativeBridge.setLoginLimitFromDefault(context)
    }

    private const val TAG = "EventService"
}