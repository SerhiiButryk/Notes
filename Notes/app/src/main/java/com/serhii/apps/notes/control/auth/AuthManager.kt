/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.auth

import com.serhii.apps.notes.control.auth.types.UIRequestType
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.core.security.Crypto
import com.serhii.core.security.Hash

/**
 * Performs app authorization request processing
 */

enum class RequestType(var description: String, var type: Int) {
    REQ_AUTHORIZE("REQ_AUTHORIZE", 1), REQ_REGISTER(
        "REQ_REGISTER",
        2
    ),
    REQ_BIOMETRIC_LOGIN("REQ_BIOMETRIC_LOGIN", 3);
}

class AuthManager {

    fun handleRequest(type: RequestType, data: AuthModel): Boolean {

        return when {
            type == RequestType.REQ_AUTHORIZE && data.authType == UIRequestType.UNLOCK -> {

                val crypto = Crypto()
                crypto.getKeyMaster().initUnlockKey(data.password)

                requestUnlock(data.password, crypto.getKeyMaster().getUnlockKey())
                // Return result
                true
            }

            type == RequestType.REQ_AUTHORIZE -> {
                /*
                    Result can be true or false
                */
                requestAuthorization(Hash.hashMD5(data.password), data.email)
            }

            type == RequestType.REQ_REGISTER ->
                /*
                    Result can be true or false
                */
                requestRegistration(
                    Hash.hashMD5(data.password),
                    Hash.hashMD5(data.confirmPassword),
                    data.email
                )

            type == RequestType.REQ_BIOMETRIC_LOGIN -> {
                requestAuthorization()
                // Return result
                true
            }

            // Return result
            else -> true
        }
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
    private external fun requestRegistration(
        password: String,
        confirmPassword: String,
        username: String
    ): Boolean

    private external fun requestUnlock(unlockKey: String, currentKey: String)
    private external fun requestAuthorization()

    private external fun verifyInput(
        password: String,
        confirmPassword: String,
        username: String
    ): Boolean

    companion object {
        const val TAG = "AuthManager"
    }
}