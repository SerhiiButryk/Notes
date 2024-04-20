/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.view_model

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.serhii.apps.notes.control.EventService
import com.serhii.apps.notes.control.auth.base.IEventService
import com.serhii.apps.notes.control.auth.types.AuthorizeType
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.core.security.BiometricAuthenticator

/**
 *  View model for managing UI state of AuthorizationActivity
 */
class LoginViewModel : ViewModel() {

    val showRegistrationUI = MutableLiveData(false)

    val authorizeService: IEventService
        get() = EventService

    fun proceedWithAuth(context: Context, authModel: AuthModel) {
        when (authModel.authType) {
            AuthorizeType.AUTH_UNLOCK, AuthorizeType.AUTH_PASSWORD_LOGIN ->
                authorizeService.onPasswordLogin(context, authModel)
            AuthorizeType.AUTH_REGISTRATION -> {
                throw RuntimeException("Not a right method")
            }
            AuthorizeType.AUTH_BIOMETRIC_LOGIN ->
                authorizeService.onBiometricLogin(context, authModel)
            else -> {
                throw RuntimeException("Unknown auth type")
            }
        }
    }

    fun proceedWithRegistration(authModel: AuthModel, biometricAuthenticator: BiometricAuthenticator?, fragmentActivity: FragmentActivity) {
        authorizeService.onRegistration(authModel, biometricAuthenticator, fragmentActivity)
    }

    fun requestRegistrationUI() {
        // Notify observers that we need to show an activity
        showRegistrationUI.value = true
        // Reset flag
        showRegistrationUI.value = false
    }
}