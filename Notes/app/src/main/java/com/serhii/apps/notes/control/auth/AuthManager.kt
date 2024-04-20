/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.auth

import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.types.AuthorizeType
import com.serhii.apps.notes.control.auth.types.RequestType
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.core.security.Hash

/**
 * Performs app authorization request processing
 */
class AuthManager {

    private val hash = Hash()

    fun handleRequest(type: RequestType, data: AuthModel): Boolean  =
        when {
            type == RequestType.REQ_AUTHORIZE && data.authType == AuthorizeType.AUTH_UNLOCK -> {
                requestUnlock(data.password, hash.hashMD5(NativeBridge.unlockKey))
                // Return result
                true
            }

            type == RequestType.REQ_AUTHORIZE -> {
                // Return result is true or false
                requestAuthorization(hash.hashMD5(data.password), data.email)
            }

            type == RequestType.REQ_REGISTER ->
                /*
                    Entered credentials will be checked on the native side
                    It will make a decision about the next step
                */

                // Return result is true or false
                requestRegistration(hash.hashMD5(data.password), hash.hashMD5(data.confirmPassword), data.email)

            type == RequestType.REQ_BIOMETRIC_LOGIN -> {
                requestAuthorization()
                // Return result
                true
            }

            // Return result
            else -> true
        }

    // Only password check , no other actions here
    fun checkInput(password: String, confirmPassword: String, username: String): Boolean {
        return verifyInput(password, confirmPassword, username)
    }

    // Updates the UI , no other actions here
    fun complete() {
        requestAuthorization()
    }

    private external fun requestAuthorization(password: String, username: String): Boolean
    private external fun requestRegistration(password: String, confirmPassword: String, username: String): Boolean
    private external fun requestUnlock(unlockKey: String, currentKey: String)
    private external fun requestAuthorization()

    private external fun verifyInput(password: String, confirmPassword: String, username: String): Boolean

    companion object {
        const val TAG = "AuthManager"
    }
}