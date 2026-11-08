package com.notes.notes_ui

import api.data.AppSettings
import com.notes.notes_ui.models.AccountInfoState
import com.notes.notes_ui.models.SettingsState
import com.notes.ui.model.BaseAppVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class SettingsVMBase(
    scopeOverride: CoroutineScope? = null,
    protected val interactor: SettingsInteractorBase,
) : BaseAppVM(scopeOverride) {

    protected val _accountInfoState = MutableStateFlow(AccountInfoState())
    val accountInfoState = _accountInfoState.asStateFlow()

    protected val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    fun onOpen() {
        updateAccountInfo()
        updateSettingsState()
    }

    open fun onDebugChanged(isDebug: Boolean, openDocTree: () -> Unit = {}) {
        scope.launch {
            AppSettings.setDebugMode(isDebug)
            updateSettingsState()
        }
    }

    // Will sign out from Google and Firebase
    fun onSignOut(onSuccess: () -> Unit) {
        scope.launch(Dispatchers.Default) {
            interactor.singOut { result ->
                if (result) {
                    launch(Dispatchers.Main) {
                        onSuccess()
                    }
                }
                updateAccountInfo()
            }
        }
    }

    fun updateAccountInfo(pending: Boolean = false, showGrantPermissions: Boolean = false) {
        scope.launch {
            var accountInfo = interactor.getAccountInfo(pending)
            if (!showGrantPermissions) {
                accountInfo = accountInfo.copy(showGrantPermissions = showGrantPermissions)
                _accountInfoState.emit(accountInfo)
            } else {
                _accountInfoState.emit(accountInfo)
            }
        }
    }

    private fun updateSettingsState() {
        scope.launch {
            _settingsState.emit(SettingsState(
                isDebug = AppSettings.isDebugEnabled
            ))
        }
    }

}