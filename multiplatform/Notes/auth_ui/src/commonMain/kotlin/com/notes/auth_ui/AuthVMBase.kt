package com.notes.auth_ui

import api.Platform
import api.auth.AuthResult
import api.utils.getErrorTitleAndMessage
import com.notes.auth_ui.data.LoginUIState
import com.notes.auth_ui.data.RegisterUIState
import com.notes.ui.model.BaseAppVM
import kotlinx.coroutines.CoroutineScope

private const val tag = "AuthVM"

open class AuthVMBase(
    // For test support
    scopeOverride: CoroutineScope? = null
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

    // Handy ui state factories

    suspend fun createLoginUIState(
        showChangePasswordUI: Boolean = false,
        uiForced: Boolean = false
    ): LoginUIState {
        return LoginUIState(
            hasFocus = true,
            email = interactor.getEmail(),
            uiForced = uiForced,
            authToConfirm = showChangePasswordUI
        )
    }

    fun createRegisterUIState(
        showChangePasswordUI: Boolean = false,
    ): RegisterUIState {
        return RegisterUIState(
            hasFocus = true,
            showChangePassword = showChangePasswordUI,
        )
    }

}