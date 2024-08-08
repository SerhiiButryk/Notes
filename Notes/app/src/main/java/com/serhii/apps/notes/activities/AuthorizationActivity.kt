/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.EventService
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.types.AuthResult
import com.serhii.apps.notes.control.auth.types.UIRequestType
import com.serhii.apps.notes.ui.AuthorizationUI
import com.serhii.apps.notes.ui.WelcomeUI
import com.serhii.apps.notes.ui.state_holders.LoginViewModel
import com.serhii.apps.notes.ui.theme.AppMaterialTheme
import com.serhii.core.log.Log
import kotlinx.coroutines.launch

/**
 * Activity which starts user authorization
 */
class AuthorizationActivity : ComponentActivity() {

    private val authViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.info(TAG, "onCreate()")

        if (savedInstanceState == null) {
            authViewModel.initViewModel(this)
        }

        configureUI()
        initNative()
    }

    private fun configureUI() {
        Log.info(TAG, "configureUI()")
        setContent {
            AppMaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    // Convert from State Flow to a State observable holder
                    val uiState = authViewModel.uiState.collectAsStateWithLifecycle()

                    val appUIState = uiState.value

                    // AuthorizationUI handles LoginUIState and RegistrationUIState states
                    if (appUIState is LoginViewModel.LoginUIState ||
                        appUIState is LoginViewModel.RegistrationUIState
                    ) {
                        AuthorizationUI(uiState = appUIState, authViewModel)
                    } else if (appUIState is LoginViewModel.WelcomeUIState) {
                        WelcomeUI(uiState = appUIState, authViewModel)
                    }
                }
            }
        }
    }

    /**
     * Called by native
     */
    fun onAuthorizationFinished() {
        Log.info(TAG, "onAuthorize(), activity is finishing")
        // Close activity
        finish()
    }

    /**
     * Called by native
     */
    fun userRegistered() {
        Log.info(TAG, "userRegistered()")

        authViewModel.proceed(requestType = UIRequestType.LOGIN_UI, context = applicationContext)

        lifecycleScope.launch(App.BACKGROUND_DISPATCHER) {
            EventService.onRegistrationDone(applicationContext)
        }
    }

    /**
     * Called by native to notify that a dialog should be shown
     */
    private fun showAlertDialog(type: Int) {
        Log.info(TAG, "showAlertDialog(), type $type")

        // Process error state
        EventService.onErrorState(type) {
            Log.detail(TAG, "showAlertDialog(), show dialog")
            authViewModel.proceed(
                requestType = UIRequestType.SHOW_DIALOG,
                context = applicationContext,
                type = type
            )
        }

        // If app gets blocked then show Block UI
        if (type == AuthResult.WRONG_PASSWORD.typeId && NativeBridge.isAppBlocked) {
            authViewModel.proceed(
                requestType = UIRequestType.BLOCK_UI,
                context = applicationContext
            )
        }
    }

    /**
     * Initialize native part
     */
    private external fun initNative()

    companion object {

        private const val TAG = "AuthorizationActivity"

        init {
            System.loadLibrary(App.RUNTIME_LIBRARY)
        }
    }

}