/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.serhii.apps.notes.control.EventService
import com.serhii.apps.notes.control.auth.base.IAuthorizeService
import com.serhii.apps.notes.control.auth.types.AuthorizeType
import com.serhii.apps.notes.ui.data_model.AuthCredsModel

/**
 * View model for authentication UI.
 */
class LoginViewModel : ViewModel() {

    val showRegistrationUI = MutableLiveData(false)

    val authorizeService: IAuthorizeService
        get() = EventService

    fun setAuthValue(newValue: AuthCredsModel) {
        // Select correct action
        when (newValue.authType) {
            AuthorizeType.AUTH_UNLOCK, AuthorizeType.AUTH_PASSWORD_LOGIN -> // Proceed with simple login
                authorizeService.onPasswordLogin(newValue)
            AuthorizeType.AUTH_REGISTRATION ->  // Proceed with User's registration
                authorizeService.onRegistration(newValue)
            AuthorizeType.AUTH_BIOMETRIC_LOGIN ->  // Proceed with simple login
                authorizeService.onBiometricLogin()
            else -> {
                throw RuntimeException("Unknown auth type")
            }
        }
    }

    fun requestRegistrationUI() {
        // Notify observers that we need to show an activity
        showRegistrationUI.value = true
        // Reset flag
        showRegistrationUI.value = false
    }
}