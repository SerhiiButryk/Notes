package com.notes.auth_ui

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavKey
import api.Platform
import com.notes.auth_ui.data.LoginUIState
import com.notes.auth_ui.data.RegisterUIState
import com.notes.ui.LoginScreen
import com.notes.ui.OnBoardingNoteScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val tag = "AuthVM"

class AuthVM(
    // For test support
    scopeOverride: CoroutineScope? = null,
) : AuthVMBase(scopeOverride) {

    private val _uiStateRegister = MutableStateFlow(RegisterUIState())
    val registerUIState = _uiStateRegister.asStateFlow()

    private val _uiStateLogin = MutableStateFlow(LoginUIState())
    val loginUIState = _uiStateLogin.asStateFlow()

    val startDestination = mutableStateOf<NavKey?>(null)

    init {
        val firstLaunch = scope.async (Dispatchers.IO) {
            !interactor.hasRegisteredUser()
        }
        scope.launch {
            startDestination.value = if (firstLaunch.await()) {
                OnBoardingNoteScreen
            } else {
                onShowLoginScreen()
                LoginScreen
            }
            Platform().logger.logi("AuthVM() init done")
        }
    }

    fun login(
        state: LoginUIState,
        onSuccess: () -> Unit,
    ) {
        Platform().logger.logi("$tag::login()")

        scope.launch {

            _uiStateLogin.showProgress(show = true)

            val result = interactor.login(password = state.password, email = state.email, null)

            if (result.isSuccess()) {
                Platform().logger.logi("$tag::login() success")
                onSuccess()
            } else {
                Platform().logger.logi("$tag::login() failed")
                _uiStateLogin.showProgress(show = false)
                handleResult(result)
            }

        }
    }

    fun register(
        state: RegisterUIState,
        onSuccess: () -> Unit,
    ) {
        Platform().logger.logi("$tag::register()")

        scope.launch {
            val result =
                interactor.register(
                    password = state.password,
                    email = state.email,
                    confirmPassword = state.confirmPassword,
                )
            if (result.isSuccess()) {
                Platform().logger.logi("$tag::register() success")
                onSuccess()
            } else {
                Platform().logger.logi("$tag::register() failed")
                handleResult(result)
            }
        }

        onSuccess()
    }

    suspend fun onShowRegisterScreen() {
        _uiStateRegister.emit(createRegisterUIState())
    }

    suspend fun onShowLoginScreen() {
        _uiStateLogin.emit(createLoginUIState())
    }

    suspend fun onOnBoardingContinue() {
        // Showing Register UI
        onShowRegisterScreen()
    }
}
