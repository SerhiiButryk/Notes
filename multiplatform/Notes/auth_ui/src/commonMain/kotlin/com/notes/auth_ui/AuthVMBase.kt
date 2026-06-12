package com.notes.auth_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.Platform
import api.auth.AuthResult
import api.utils.getErrorTitleAndMessage
import com.notes.auth_ui.data.DialogState
import com.notes.auth_ui.data.LoginUIState
import com.notes.auth_ui.data.RegisterUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val tag = "AuthVM"

open class AuthVMBase(
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel() {

    protected val scope: CoroutineScope = scopeOverride ?: viewModelScope

    protected val interactor = Interactor()

    // Dialog ui state
    protected val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState = _dialogState.asStateFlow()

    protected open suspend fun handleResult(result: AuthResult) {
        Platform().logger.logi("$tag::handleResult()")

        // Handle other errors
        val strings = getErrorTitleAndMessage(result)
        val title = strings.first
        val subtitle = strings.second

        showDialog(title = title, subtitle = subtitle)
    }

    protected suspend fun showDialog(
        title: String,
        subtitle: String,
        onConfirm: (() -> Unit)? = null
    ) {
        _dialogState.emit(
            DialogState(title = title, subtitle = subtitle, onConfirm = onConfirm)
        )
    }

    fun dismissDialog() {
        Platform().logger.logi("$tag::dismissDialog()")
        scope.launch {
            _dialogState.emit(null)
        }
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