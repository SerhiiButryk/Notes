package com.notes.notes_ui

import api.Platform
import api.data.AppSettings
import api.repo.Repository
import com.notes.notes_ui.models.AccountInfoState
import com.notes.notes_ui.models.SettingsState
import com.notes.ui.model.BaseAppVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsVM(
    repository: Repository = Platform().appRepo,
    scopeOverride: CoroutineScope? = null,
) : BaseAppVM(scopeOverride) {

    private val _accountInfoState = MutableStateFlow(AccountInfoState())
    val accountInfoState = _accountInfoState.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    private val interactor = SettingsInteractorBase(repository)

    fun onOpen() {
        updateAccountInfo()
        updateSettingsState()
    }

    fun onDebugChanged(isDebug: Boolean) {
        AppSettings.isDebugEnabled = isDebug
        updateSettingsState()
    }

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

    private fun updateAccountInfo() {
        scope.launch {
            var accountInfo = interactor.getAccountInfo()
            accountInfo = accountInfo.copy(showGrantPermissions = false)
            _accountInfoState.emit(accountInfo)
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