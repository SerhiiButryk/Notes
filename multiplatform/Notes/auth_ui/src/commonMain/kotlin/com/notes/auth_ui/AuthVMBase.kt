package com.notes.auth_ui

import api.Platform
import api.auth.AuthResult
import api.utils.getErrorTitleAndMessage
import com.notes.auth_ui.data.LoginUIState
import com.notes.auth_ui.data.RegisterUIState
import com.notes.auth_ui.data.UIState
import com.notes.auth_ui.data.copyLoginUIState
import com.notes.ui.model.BaseAppVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

private const val tag = "AuthVM"

open class AuthVMBase(
    // For test support
    scopeOverride: CoroutineScope? = null,
) : BaseAppVM(scopeOverride) {
    protected val interactor = Interactor()

    protected open suspend fun handleResult(result: AuthResult) {
        Platform().logger.logi("$tag::handleResult()")

        // Handle other errors
        val strings = getErrorTitleAndMessage(result)
        val title = strings.first
        val subtitle = strings.second

        showDialog(title = title, subtitle = subtitle)
    }

    override fun onCleared() {
        Platform().logger.logi("$tag::onCleared()")
        interactor.onClear()
    }

    // ui state factories

    suspend fun createLoginUIState(
        showChangePasswordUI: Boolean = false,
        uiForced: Boolean = false,
    ): LoginUIState =
        LoginUIState(
            hasFocus = true,
            email = interactor.getEmail(),
            uiForced = uiForced,
            authToConfirm = showChangePasswordUI,
        )

    fun createRegisterUIState(showChangePasswordUI: Boolean = false): RegisterUIState =
        RegisterUIState(
            hasFocus = true,
            showChangePassword = showChangePasswordUI,
        )
}

// ui state change

suspend fun MutableStateFlow<LoginUIState>.showProgress(show: Boolean) {
    val newState = copyLoginUIState(showProgress = show)
    emit(newState as LoginUIState)
}

suspend fun MutableStateFlow<UIState>.showProgressSafe(show: Boolean) {
    val newState = copyLoginUIState(showProgress = show)
    if (newState != null) {
        emit(newState)
    }
}
