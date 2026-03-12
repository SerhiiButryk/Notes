package com.notes.notes_ui

import api.AppService.Companion.FIREBASE_AUTH
import api.AppServices
import api.Platform
import api.data.AppSettings
import api.repo.Repository
import com.notes.notes_ui.models.AccountInfoState
import com.notes.notes_ui.models.SettingsState
import com.notes.ui.model.BaseAppVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsVM(
    private val repository: Repository = Platform().appRepo,
    scopeOverride: CoroutineScope? = null,
) : BaseAppVM(scopeOverride) {

    private val _accountInfoState = MutableStateFlow(AccountInfoState())
    val accountInfoState = _accountInfoState.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    fun onOpen() {
        updateAccountInfo()
        updateSettingsState()
    }

    fun onDebugChanged(isDebug: Boolean) {
        AppSettings.isDebugEnabled = isDebug
        updateSettingsState()
    }

    fun onSignOut() {

    }

    private fun updateAccountInfo() {
        scope.launch {
            val accountInfo = getAccountInfo()
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

    suspend fun getAccountInfo(): AccountInfoState {

        val email =
            AppServices
                .getDefaultAuthService()
                .getUserEmail()

        val firebaseIsActive =
            AppServices
                .getAuthServiceByKey(FIREBASE_AUTH)
                .isAuthenticated()

        return AccountInfoState(
            email = email,
            firebaseIsActive = firebaseIsActive,
            syncCompleted = repository.isDataInSync(),
        )

    }
}