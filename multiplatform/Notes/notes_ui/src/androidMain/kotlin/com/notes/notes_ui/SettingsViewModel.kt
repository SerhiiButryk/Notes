package com.notes.notes_ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import api.Platform
import api.data.AppSettings
import api.repo.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    appRepository: Repository = Platform().appRepo,
    // For test support
    scopeOverride: CoroutineScope? = null,
) : SettingsVMBase(
    scopeOverride = scopeOverride,
    interactor = SettingsInteractor(appRepository)
) {

    init {
        updateAccountInfo()
    }

    // Request permissions for Google Drive
    fun requestPermissions(
        context: Any?,
        onSuccess: (Any?) -> Unit,
    ) {
        scope.launch {
            updateAccountInfo(pending = true)
            interactor.requestPermissions(context, onSuccess) {
                updateAccountInfo()
            }
        }
    }

    fun onExport(
        uri: Uri?,
        context: Context,
    ) {
        Platform().logger.logi("onExport()")
        if (uri == null) {
            Platform().logger.loge("onExport() error: uri is null")
            return
        }
        scope.launch(Dispatchers.Default) {
            interactor.onExport(uri, context)
        }
    }

    override fun onDebugChanged(isDebug: Boolean, openDocTree: () -> Unit) {
        super.onDebugChanged(isDebug, openDocTree)
        scope.launch {
            if (isDebug && !Platform().logger.hasLogFile()) {
                openDocTree()
            }
        }
    }

    @SuppressLint("WrongConstant")
    fun onSelectFolder(uri: Uri?, context: Context) {
        Platform().logger.logi("onSelectFolder()")

        if (uri == null) {
            Platform().logger.loge("onSelectFolder() error: uri is null")
            return
        }

        try {
            // Persist the permission so it survives app restarts
            context.contentResolver.takePersistableUriPermission(uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            Platform().logger.loge("onSelectFolder() error: $e")
            return
        }

        val uri = (interactor as SettingsInteractor).createNewFileInTree(
            context = context,
            treeUri = uri,
            mimeType = "text/plain",
            displayName = Platform().logger.getLogFileName(),
        )

        if (uri != null) {
            scope.launch(Dispatchers.Default) {
                AppSettings.setLogFilePath(uri.toString())
                Platform().logger.logi("onSelectFolder() saved uri")
            }
        }

    }

}
