/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.state_holders

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.ui.DialogUIState
import com.serhii.core.log.Log
import kotlinx.coroutines.launch

/**
 *  View model for reusing UI logic of application
 */

private const val TAG = "AppViewModel"

open class AppViewModel : ViewModel() {

    private var immManager: InputMethodManager? = null

    open fun initViewModel(context: Context) {
        immManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    fun requestKeyboard(focusRequester: FocusRequester) {
        viewModelScope.launch(App.UI_DISPATCHER) {
            Log.detail(TAG, "requestKeyboard() try to show keyboard")
            if (immManager != null && !immManager!!.isAcceptingText()) {
                focusRequester.requestFocus()
                Log.detail(TAG, "requestKeyboard() done")
            }
        }
    }

    fun requestDialog(
        title: Int,
        message: Int,
        onConfirm: () -> Unit,
        onCancel: () -> Unit,
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
}