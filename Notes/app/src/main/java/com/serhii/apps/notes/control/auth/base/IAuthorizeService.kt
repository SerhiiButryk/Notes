/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.auth.base

import android.content.Context
import com.serhii.apps.notes.ui.data_model.AuthModel

/**
 * The interface to handle user authentication events
 */
interface IAuthorizeService {
    fun onPasswordLogin(model: AuthModel)
    fun onRegistration(model: AuthModel)
    fun onBiometricLogin()
    fun onUserRegistered(context: Context)
}