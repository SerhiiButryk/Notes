package com.notes.notes_ui

import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.AppServices
import api.auth.AuthCallback
import com.notes.data.isAllInSyncWithRemote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel() {

    data class AccountInfo(
        val email: String = "",
        val googleIsActive: Boolean = false,
        val firebaseIsActive: Boolean = false,
        val googleDriveIsActive: Boolean = false,
        val syncCompleted: Boolean = false,
        val showGrantPermissions: Boolean = false,
        val pending: Boolean = false,
    )

    private val _accountInfo = MutableStateFlow(AccountInfo())
    val accountInfo = _accountInfo.asStateFlow()

    private val scope: CoroutineScope = scopeOverride ?: viewModelScope

    init {
        updateAccountInfo()
    }

    // Request permissions for Google Drive
    fun requestPermissions(context: Any?, onSuccess: (IntentSenderRequest) -> Unit) {
        scope.launch {
            updateAccountInfo(pending = true)
            val service = AppServices
                .getAuthServiceByName("google")!!
            service.resetSettings()
            val callback = object : AuthCallback {
                override fun onUserAction(data: Any?) {
                    if (data != null) {
                        onSuccess(data as IntentSenderRequest)
                    }
                    service.setAuthCallback(null)
                    updateAccountInfo()
                }
            }
            service.setAuthCallback(callback)
            // Will sign in and ask permission from user
            service.login(activityContext = context, pass = "", email = "")
        }
    }

    // Will sign out from Google and Firebase
    fun singOut() {
        scope.launch {
            AppServices
                .getAuthServiceByName("google")!!
                .signOut()
            updateAccountInfo()
        }
    }

    fun updateAccountInfo(pending: Boolean = false) {
        scope.launch {
            val email = AppServices
                .getDefaultAuthService()!!
                .getUserEmail()

            val googleIsActive = AppServices
                .getAuthServiceByName("google")!!
                .isAuthenticated()

            val firebaseIsActive = AppServices
                .getAuthServiceByName("firebase")!!
                .isAuthenticated()

            val googleDriveIsActive = AppServices
                .getStoreService("googledrive")!!
                .canUse

            val grantPermission = !googleDriveIsActive

            val syncCompleted = isAllInSyncWithRemote()

            _accountInfo.update { AccountInfo(
                email = email,
                googleIsActive = googleIsActive,
                firebaseIsActive = firebaseIsActive,
                googleDriveIsActive = googleDriveIsActive,
                showGrantPermissions = grantPermission,
                pending = pending,
                syncCompleted = syncCompleted
            ) }
        }
    }
}