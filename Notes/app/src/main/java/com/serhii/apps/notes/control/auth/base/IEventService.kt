/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.auth.base

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.core.security.BiometricAuthenticator

/**
 * The interface to handle user events
 */
interface IEventService {
    fun onPasswordLogin(context: Context, model: AuthModel)
    fun onRegistration(model: AuthModel, biometricAuthenticator: BiometricAuthenticator?, fragmentActivity: FragmentActivity)
    fun onBiometricLogin(context: Context, authModel: AuthModel)
    fun onRegistrationFinished(context: Context)
    fun onChangePassword(newPassword: String): Boolean
    fun onChangeLoginLimit(newLimit: Int)
    fun onShowAlertDialog(context: Context, type: Int)
}