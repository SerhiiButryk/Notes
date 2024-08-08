/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.auth.base

import android.content.Context
import com.serhii.apps.notes.ui.data_model.AuthModel
import javax.crypto.Cipher

/**
 * The interface to handle UI events
 */
interface IEventService {

    suspend fun onPasswordLogin(context: Context, model: AuthModel)

    suspend fun onRegistration(
        model: AuthModel, hasBiometric: Boolean, showBiometricDialog: suspend () -> Unit
    )

    fun onRegistrationDone(
        completedSuccessfully: Boolean,
        cipher: Cipher? = null,
        authModel: AuthModel
    )

    suspend fun onBiometricLogin(authModel: AuthModel, showMessage: (id: Int) -> Unit = {})

    fun onRegistrationDone(context: Context)

    fun onChangePassword(
        oldPassword: String,
        newPassword: String,
        showMessage: (id: Int) -> Unit
    ): Boolean

    fun onChangeLoginLimit(newLimit: Int)

    fun onErrorState(type: Int, dialogCallback: () -> Unit)
}