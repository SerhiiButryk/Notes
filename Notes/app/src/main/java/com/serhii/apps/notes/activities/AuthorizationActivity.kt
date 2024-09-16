/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.EventService
import com.serhii.apps.notes.control.auth.types.UIRequestType
import com.serhii.apps.notes.ui.AuthorizationUI
import com.serhii.apps.notes.ui.WelcomeUI
import com.serhii.apps.notes.ui.state_holders.LoginViewModel
import com.serhii.apps.notes.ui.theme.AppMaterialTheme
import com.serhii.core.log.Log
import kotlinx.coroutines.launch

/**
 * Activity which performs user authorization
 */
class AuthorizationActivity : AppBaseActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLoggingTagForActivity(TAG)
        super.onCreate(savedInstanceState)
        Log.info(TAG, "onCreate()")

        if (savedInstanceState == null) {
            viewModel.initViewModel(this)
        }

        viewModel.setupBiometrics(this)

        setupUI()
        initNative()
    }

    private fun setupUI() {
        setContent {
            AppMaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.safeDrawingPadding()) {

                        // Convert from State Flow to a State observable holder
                        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                        val appUIState = uiState.value

                        // AuthorizationUI handles LoginUIState and RegistrationUIState states
                        if (appUIState is LoginViewModel.LoginUIState ||
                            appUIState is LoginViewModel.RegistrationUIState
                        ) {
                            AuthorizationUI(uiState = appUIState, viewModel)
                        } else if (appUIState is LoginViewModel.WelcomeUIState) {
                            WelcomeUI(uiState = appUIState, viewModel)
                        }
                    }
                }
            }
        }
    }

    /**
     * Called by native to notify that user has been logged in
     */
    fun onAuthorizationFinished() {
        Log.info(TAG, "onAuthorize()")
        // Close this activity
        finish()
    }

    /**
     * Called by native to notify that registration has completed
     */
    fun userRegistered() {
        Log.info(TAG, "userRegistered()")

        viewModel.proceed(requestType = UIRequestType.LOGIN_UI, context = applicationContext)

        lifecycleScope.launch(App.BACKGROUND_DISPATCHER) {
            EventService.onRegistrationDone(applicationContext)
        }
    }

    /**
     * Called by native to notify that we need to show a dialog
     */
    private fun showAlertDialog(type: Int) {
        Log.info(TAG, "showAlertDialog(), type $type")
        // Show a dialog
        viewModel.proceed(
            requestType = UIRequestType.SHOW_DIALOG,
            context = applicationContext,
            type = type
        )
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