/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.state_holders

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.serhii.apps.notes.ui.SettingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference

class SettingsViewModel : ViewModel() {

    // UI state observable data
    private val _uiState = MutableStateFlow(SettingsUIState(emptyList()))
    val uiState: StateFlow<SettingsUIState> = _uiState

    private var activityRef: WeakReference<Activity> = WeakReference(null)

    ///////////////////////////// UI State class /////////////////////////////

    class SettingsUIState(val items: List<SettingItem>)

    //////////////////////////////////////////////////////////////////////////

    fun initViewModel(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    // In case of back navigation we just close the activity
    fun navigateBack() {
        activityRef.get()?.finish()
    }

    fun updateState(items: List<SettingItem>) {
        _uiState.value = SettingsUIState(items)
    }

}