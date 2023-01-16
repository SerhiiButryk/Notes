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
import com.serhii.apps.notes.ui.data_model.AuthModel

/**
 * View model for managing authentication data and state.
 */
class LoginViewModel : ViewModel() {

    val showRegistrationUISetFlag = MutableLiveData(false)

    val authorizeService: IAuthorizeService
        get() = EventService

    fun setAuthValue(newValue: AuthModel?) {
        // If data is set then perform correct action
        if (newValue != null) {
            when (newValue.authType) {
                AuthorizeType.AUTH_UNLOCK, AuthorizeType.AUTH_PASSWORD_LOGIN -> // Proceed with Login
                    authorizeService.onPasswordLogin(newValue)
                AuthorizeType.AUTH_REGISTRATION ->  // Proceed with User's registration
                    authorizeService.onRegistration(newValue)
                AuthorizeType.AUTH_BIOMETRIC_LOGIN ->  // Proceed with Login
                    authorizeService.onBiometricLogin()
                else -> {
                    throw RuntimeException("Unknown auth type")
                }
            }
        }
    }

    fun showRegistrationUI() {
        // Notify observers that action should be performed
        showRegistrationUISetFlag.value = true
        // Reset flag to notify that activity doesn't need to perform any action
        showRegistrationUISetFlag.value = false
    }
}