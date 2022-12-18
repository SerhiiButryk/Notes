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

    fun handleRequest(type: RequestType, data: AuthModel?) {
        if (type == RequestType.REQ_AUTHORIZE && data != null && data.authType == AuthorizeType.AUTH_UNLOCK) {
            requestUnlock(data.password, Hash().hashMD5(NativeBridge().unlockKey))
        } else if (type == RequestType.REQ_AUTHORIZE && data != null) {
            requestAuthorization(data.password, data.email)
        } else if (type == RequestType.REQ_REGISTER && data != null) {
            /*
                Entered credentials will be checked on the native side
                It will make a decision about the next step
            */
            requestRegistration(data.password, data.confirmPassword, data.email)
        } else if (type == RequestType.REQ_BIOMETRIC_LOGIN) {
            requestBiometricLogin()
        }
    }

    private external fun requestAuthorization(password: String, username: String)
    private external fun requestRegistration(password: String, confirmPassword: String, username: String)
    private external fun requestUnlock(unlockKey: String, currentKey: String)
    private external fun requestBiometricLogin()

    companion object {
        const val TAG = "AuthManager"
    }
}