/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.auth.base

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.serhii.apps.notes.ui.data_model.AuthModel
import com.serhii.core.security.BiometricAuthenticator
import kotlinx.coroutines.CoroutineScope

/**
 * The interface to handle user events
 */
interface IEventService {
    suspend fun onPasswordLogin(context: Context, model: AuthModel)
    suspend fun onRegistration(model: AuthModel, biometricAuthenticator: BiometricAuthenticator?,
                               fragmentActivity: FragmentActivity, coroutineScope: CoroutineScope)
    suspend fun onBiometricLogin(context: Context, authModel: AuthModel)
    fun onRegistrationDone(context: Context)
    fun onChangePassword(newPassword: String): Boolean
    fun onChangeLoginLimit(newLimit: Int)
    fun onErrorState(type: Int, showDialog: () -> Unit)
}