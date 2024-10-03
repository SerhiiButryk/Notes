/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.state_holders

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.ui.DialogUIState
import com.serhii.apps.notes.ui.state_holders.NotesViewModel.NotesEditorUIState
import com.serhii.core.log.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 *  View model for reusing UI logic of the application
 */

private const val TAG = "AppViewModel"

open class AppViewModel : ViewModel() {

    private var immManager: InputMethodManager? = null
    protected var activityRef: WeakReference<Activity>? = null

    open fun initViewModel(activity: Activity) {
        immManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        activityRef = WeakReference(activity)
    }

    open fun backupNote(uiState: NotesEditorUIState) {
        val activity = activityRef?.get()
        if (activity != null) {
            BackupManager.openDirectoryChooserForExtractData(activity)
        } else {
            Log.error(TAG, "backupNote() failed activity ref is null")
        }
    }

    fun requestKeyboard(focusRequester: FocusRequester) {
        viewModelScope.launch(App.UI_DISPATCHER) {
            Log.detail(TAG, "requestKeyboard() try to show keyboard")
            if (immManager != null) {
                try {
                    focusRequester.requestFocus()
                    Log.detail(TAG, "requestKeyboard() done")
                } catch (e: IllegalStateException) {
                    // Might be in case of note deletion
                    Log.detail(TAG, "requestKeyboard() cannot show keyboard this time")
                }
            }
        }
    }

    fun requestDialog(
        title: Int,
        message: Int,
        onConfirm: () -> Unit = {},
        onCancel: () -> Unit= {},
        positiveBtn: Int = android.R.string.ok,
        negativeBtn: Int = android.R.string.cancel,
        hasCancelButton: Boolean = true
    ): DialogUIState {
        return DialogUIState(
            title = title,
            message = message,
            onConfirm = onConfirm,
            onCancel = onCancel,
            hasCancelButton = hasCancelButton,
            dialogDismissible = !hasCancelButton,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn
        )
    }

    suspend fun showStatusMessage(context: Context, result: Boolean) {
        withContext(App.UI_DISPATCHER) {
            val message = if (result) R.string.result_success else R.string.result_failed
            showMessage(context, message)
        }
    }

    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showMessage(context: Context, stringId: Int) {
        Toast.makeText(context, context.getString(stringId), Toast.LENGTH_SHORT).show()
    }
}