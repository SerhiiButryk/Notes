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
        var dialogState: DialogUIState = DialogUIState()
    )

    class DialogOption(val text: Int, val isSelected: Boolean = false, val onSelected: () -> Unit)

    class ListDialogUIState(
        val listOptions: List<DialogOption>,
        title: Int,
        onCancel: () -> Unit
    ) : DialogUIState(title = title, onCancel = onCancel)

    class TextInputDialogUIState(
        var inputFirst: String = "",
        var inputSecond: String = "",
        title: Int,
        onCancel: () -> Unit,
        onConfirm: () -> Unit,
        val firstHint: Int = R.string.pass_hint,
        val firstLabel: Int = R.string.pass_label,
        val secondHint: Int = R.string.conf_hint,
        val secondLabel: Int = R.string.conf_label,
        val hasSecondInput: Boolean = true,
    ) : DialogUIState(title = title, onCancel = onCancel, onConfirm = onConfirm) {

        fun checkInput(): Boolean {

            // Basic check

            if (!hasSecondInput && inputFirst.isNotEmpty())
                return true

            if (inputFirst.isEmpty() || inputSecond.isEmpty())
                return false

            if (inputFirst != inputSecond)
                return false

            return true
        }
    }

    //////////////////////////////////////////////////////////////////////////

    // In case of back navigation we just close the activity
    fun navigateBack() {
        activityRef?.get()?.finish()
    }

    fun updateState(items: List<SettingItem>) {
        this.items = items
        _uiState.value = createSettingsUIState()
    }

    private fun createSettingsUIState(): SettingsUIState {
        return SettingsUIState(items!!)
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

    // DIALOGS

    ////////////////////////////////////////////////////////////////////////////

    fun closeDialog() {
        // TODO: Doest not look good, might be revisited later
        // A hack to close dialog
        val state = createSettingsUIState().apply {
            dialogState.openDialog = false
        }
        _uiState.value = state
    }

    fun openKeywordSetDialog(context: Context, intent: Intent) {
        Log.info("SettingsViewModel", "openKeywordSetDialog()")
        openDialog(TextInputDialogUIState(
            title = R.string.set_password_dialog_title,
            onCancel = {
                closeDialog()
            },
            onConfirm = {

                val dialogState = (uiState.value.dialogState as TextInputDialogUIState)
                val result = dialogState.checkInput()

                if (result) {
                    Log.info("SettingsViewModel", "TextInputDialogUIState::onConfirm() 1")
                    closeDialog()
                    // Start backup
                    onBackup(context, dialogState.inputFirst, intent)
                } else {
                    Log.info("SettingsViewModel", "TextInputDialogUIState::onConfirm() 0")
                    // TODO:
                    // Localize
                    showMessage(context, "Keywords do not match or fields ar empty")
                }
            }
        ))
    }

    private fun openDialog(state: DialogUIState) {

        val newState = createSettingsUIState()

        newState.dialogState = state

        newState.dialogState.openDialog = true
        _uiState.value = newState
    }

    fun openKeywordRequestDialog(context: Context, intent: Intent) {
        Log.info("SettingsViewModel", "openKeywordRequestDialog()")
        openDialog(TextInputDialogUIState(
            title = R.string.keyword_dialog_title,
            onCancel = {
                closeDialog()
            },
            onConfirm = {

                val dialogState = (uiState.value.dialogState as TextInputDialogUIState)
                val result = dialogState.checkInput()

                if (result) {
                    Log.info("SettingsViewModel", "TextInputDialogUIState::onConfirm() 1")
                    closeDialog()
                    // Start restore
                    onRestore(context, dialogState.inputFirst, intent)
                } else {
                    Log.info("SettingsViewModel", "TextInputDialogUIState::onConfirm() 0")
                    // TODO:
                    // Localize
                    showMessage(context, "Keywords do not match or fields ar empty")
                }
            },
            hasSecondInput = false
        ))
    }


    fun openOptionListDialog(listOptions: List<DialogOption>) {
        openDialog(ListDialogUIState(
            listOptions = listOptions,
            title = R.string.preference_idle_lock_timeout_title,
            onCancel = {
                closeDialog()
            }
        ))
    }

}