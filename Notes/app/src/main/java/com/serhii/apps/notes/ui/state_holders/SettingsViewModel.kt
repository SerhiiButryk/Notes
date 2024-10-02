/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.state_holders

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.DialogUIState
import com.serhii.apps.notes.ui.SettingItem
import com.serhii.core.log.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream

class SettingsViewModel : AppViewModel() {

    // UI state observable data
    private val _uiState = MutableStateFlow(SettingsUIState(emptyList()))
    val uiState: StateFlow<SettingsUIState> = _uiState

    private var items: List<SettingItem>? = null

    ///////////////////////////// UI State class /////////////////////////////

    class SettingsUIState(
        val items: List<SettingItem>,
        var openDialog: Boolean = false,
        var dialogState: DialogUIState = DialogUIState()
    )

    class DialogOption(val text: Int, var checked: Boolean = false)

    class OptionsListDialogUIState(
        val listOptions: List<DialogOption>,
        val onSelected: (Int) -> Unit,
        title: Int,
        cancel: () -> Unit
    ) : DialogUIState(title = title, onCancel = cancel)

    //////////////////////////////////////////////////////////////////////////

    // In case of back navigation we just close the activity
    fun navigateBack() {
        activityRef?.get()?.finish()
    }

    fun updateState(items: List<SettingItem>) {
        this.items = items
        _uiState.value = createSettingsUIState()
    }

    fun openOptionListDialog(listOptions: List<DialogOption>, onSelected: (Int) -> Unit) {

        val newState = createSettingsUIState()
        newState.openDialog = true

        newState.dialogState = OptionsListDialogUIState(
            listOptions = listOptions,
            onSelected = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                val state = createSettingsUIState()
                state.openDialog = false
                _uiState.value = state

                onSelected(it)
            },
            title = R.string.preference_idle_lock_timeout_title,
            cancel = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                val state = createSettingsUIState()
                state.openDialog = false
                _uiState.value = state
            }
        )

        _uiState.value = newState
    }

    private fun createSettingsUIState(): SettingsUIState {
        return SettingsUIState(items!!)
    }

    fun openKeywordSetDialog() {
        // TODO: Show dialog
    }

    fun openKeywordRequestDialog() {
        // TODO: Show dialog
    }

    ///////////////////////////////////// Settings options ///////////////////////////////////////

    fun onBackup(context: Context, keyword: String, intent: Intent) {
        val outputStream: OutputStream? = try {
            val uri = intent.data
            if (uri != null) {
                context.contentResolver?.openOutputStream(uri)
            } else null
        } catch (e: FileNotFoundException) {
            Log.error("SettingsViewModel", "onActivityResult() error: $e")
            e.printStackTrace()
            return
        }

        if (outputStream == null) {
            Log.error("SettingsViewModel", "onActivityResult() error: outputStream == null")
            return
        }

        // Start backup
        viewModelScope.launch(App.BACKGROUND_DISPATCHER) {
            BackupManager.backupNotes(keyword, outputStream) { result ->
                showStatusMessage(context, result)
            }
        }
    }

    fun onExtract(context: Context, intent: Intent) {

        val outputStream: OutputStream? = try {
            context.contentResolver.openOutputStream(intent.data!!)
        } catch (e: FileNotFoundException) {
            Log.error("SettingsViewModel", "onActivityResult() failed to get output stream, error: $e")
            e.printStackTrace()
            return
        }

        // Start extract
        viewModelScope.launch(App.BACKGROUND_DISPATCHER) {
            if (UserNotesDatabase.recordsCount != 0) {
                val notes = UserNotesDatabase.getRecords()
                BackupManager.extractNotes(outputStream, notes) { result ->
                    showStatusMessage(context, result)
                }
            } else {
                Log.info("SettingsViewModel", "onActivityResult() no data")
            }
        }
    }

    fun onRestore(context: Context, keyword: String, intent: Intent) {

        val inputStream: InputStream? = try {
            context.contentResolver?.openInputStream(intent.data!!)
        } catch (e: FileNotFoundException) {
            Log.error("SettingsViewModel", "onActivityResult() error: $e")
            e.printStackTrace()
            return
        }

        if (inputStream == null) {
            Log.error("SettingsViewModel", "onActivityResult() error: outputStream == null")
            return
        }

        // Start restore
        viewModelScope.launch(App.BACKGROUND_DISPATCHER) {
            BackupManager.restoreNotes(keyword, inputStream) { result ->
                withContext(App.UI_DISPATCHER) {
                    showStatusMessage(context, result)
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

}