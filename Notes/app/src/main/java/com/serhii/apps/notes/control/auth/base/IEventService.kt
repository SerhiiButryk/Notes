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
        model: AuthModel, hasBiometric: Boolean, requestBiometricDialog: suspend () -> Unit
    )

    fun onBiometricsDone(
        completedSuccessfully: Boolean,
        cipher: Cipher? = null,
        authModel: AuthModel
    )

    suspend fun onBiometricLogin(authModel: AuthModel, requestMessage: suspend () -> Unit = {})

    fun onChangeLoginLimit(newLimit: Int)

    fun onErrorState()

    suspend fun onPasswordChange(
        context: Context,
        model: AuthModel,
        hasBiometric: Boolean,
        requestBiometricDialog: suspend () -> Unit
    )
}