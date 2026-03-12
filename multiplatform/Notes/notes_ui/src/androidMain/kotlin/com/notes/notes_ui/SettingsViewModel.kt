package com.notes.notes_ui

import android.content.Context
import android.net.Uri
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.PlatformAPIs.logger
import com.notes.notes_ui.data.AccountInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel() {

    private val _accountInfo = MutableStateFlow(AccountInfo())
    val accountInfo = _accountInfo.asStateFlow()

    private val scope: CoroutineScope = scopeOverride ?: viewModelScope

    private val interactor = SettingsInteractor()

    init {
        updateAccountInfo()
    }

    // Request permissions for Google Drive
    fun requestPermissions(context: Any?, onSuccess: (IntentSenderRequest) -> Unit) {
        scope.launch {
            updateAccountInfo(pending = true)
            interactor.requestPermissions(context, onSuccess) {
                updateAccountInfo()
            }
        }
    }

    // Will sign out from Google and Firebase
    fun singOut() {
        scope.launch {
            interactor.singOut { updateAccountInfo() }
        }
    }

    fun onExport(uri: Uri?, context: Context) {

        logger.logi("onExport() uri = ${uri != null}")

        if (uri == null) {
            logger.loge("onExport() error: uri is empty")
            return
        }

        scope.launch(Dispatchers.Default) {
            interactor.onExport(uri, context)
        }
    }

    fun updateAccountInfo(pending: Boolean = false) {
        scope.launch {
            val accountInfo = interactor.getAccountInfo(pending)
            _accountInfo.update { accountInfo }
        }
    }
}