/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.state_holders

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import com.serhii.apps.notes.R
import com.serhii.apps.notes.activities.SettingsActivity
import com.serhii.apps.notes.common.App.BACKGROUND_DISPATCHER
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.repository.NotesRepository
import com.serhii.apps.notes.ui.DialogUIState
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

/**
 *  View model for managing UI state and business logic of NotesViewActivity
 */
private const val TAG = "NotesViewModel"

class NotesViewModel : AppViewModel() {

    // UI state observable data
    private val _uiState = MutableStateFlow(BaseUIState())
    val uiState: StateFlow<BaseUIState> = _uiState

    private var latestNotes: List<NoteModel> = emptyList()
    var editorState: RichTextState? = null

    ///////////////////////////// UI State class /////////////////////////////

    open class BaseUIState(
        var openDialog: Boolean = false,
        var dialogState: DialogUIState = DialogUIState()
    )

    class NotesMainUIState(val notes: List<NoteModel> = emptyList()) : BaseUIState()

    class NotesEditorUIState(val note: NoteModel = NoteModel()) : BaseUIState()

    //////////////////////////////////////////////////////////////////////////

    private val notesRepository: NotesRepository = NotesRepository()

    private val backupDataObserver: Observer<Boolean> =
        Observer { shouldUpdateData ->
            if (shouldUpdateData) {
                Log.info(TAG, "onChanged() got data change event, retrieving new data")
                reloadData()
                BackupManager.onDataUpdated()
            }
        }

    init {
        _uiState.value = createNotesMainUIState()
        // Subscribe for updates from backup manager
        BackupManager.getUpdateDataFlagData().observeForever(backupDataObserver)
        Log.info(TAG, "NotesViewModel(), instance is created")
    }

    override fun initViewModel(context: Context) {
        super.initViewModel(context)
        // Init database
        UserNotesDatabase.init(context.applicationContext)
    }

    override fun onCleared() {
        super.onCleared()
        notesRepository.close()
        BackupManager.getUpdateDataFlagData().removeObserver(backupDataObserver)
        Log.info(TAG, "onCleared()")
    }

    fun reloadData() {
        viewModelScope.launch(BACKGROUND_DISPATCHER) {
            val notesList = notesRepository.getAll()

            latestNotes = notesList

            if (_uiState.value is NotesMainUIState) {
                _uiState.value = createNotesMainUIState()
            }
        }
    }

    fun deleteNote(noteModel: NoteModel) {

        val deleteNote = {
            viewModelScope.launch(BACKGROUND_DISPATCHER) {
                val result = notesRepository.delete(noteModel.id)
                Log.info(TAG, "deleteNote() id = ${noteModel.id} result = $result")
            }
        }

        openDialog(
            title = R.string.confirm_dialog_delete_note_title,
            message = R.string.confirm_dialog_delete_note_message,
            onConfirm = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                _uiState.value = createNewUiState()

                // TODO: Test
//                deleteNote()
            },
            onCancel = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                _uiState.value = createNewUiState()
            }
        )
    }

    fun saveNote(noteModel: NoteModel) {
        val saveNote = {
            viewModelScope.launch(BACKGROUND_DISPATCHER) {
                Log.info(TAG, "saveNote() id = ${noteModel.id}")

                val id = noteModel.id
                if (id.isEmpty()) {
                    val newId = notesRepository.add(noteModel)

                    if (newId != -1) {
                        Log.info(TAG, "saveNote() success")
                    } else {
                        Log.info(TAG, "saveNote() failed")
                    }

                    return@launch
                }

                // If id is not empty then we going to update existing note

                val note = notesRepository.get(id)

                if (!note.isEmpty) {
                    val result = notesRepository.update(id, noteModel)
                    if (result)
                        Log.info(TAG, "saveNote() updated note by id = $id")
                    else
                        Log.info(TAG, "saveNote() failed to update note by id = $id")
                }
            }
        }

        openDialog(
            title = R.string.confirm_dialog_save_note_title,
            message = R.string.confirm_dialog_save_note_message,
            onConfirm = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                _uiState.value = createNewUiState()

                // TODO: Test
//                saveNote()
            },
            onCancel = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                _uiState.value = createNewUiState()
            }
        )
    }

    fun backupNote(noteModel: NoteModel) {
        TODO("Not yet implemented")
    }

    fun openNoteEditorUI(noteModel: NoteModel? = null) {
        if (noteModel == null) {
            _uiState.value = NotesEditorUIState()
        } else {
            _uiState.value = NotesEditorUIState(noteModel)
        }
    }

    fun navigateBack(): Boolean {
        val currentUIState = _uiState.value

        if (currentUIState is NotesEditorUIState) {
            // Go to previous screen
            _uiState.value = createNotesMainUIState()
            return true
        }

        return false
    }

    fun openSettings(activity: Activity) {
        activity.startActivity(Intent(activity, SettingsActivity::class.java))
    }

    private fun createNotesMainUIState(): NotesMainUIState {
        return NotesMainUIState(latestNotes)
    }

    private fun createNotesEditorUIState(): NotesEditorUIState {
        return NotesEditorUIState()
    }

    private fun createNewUiState(): BaseUIState {
        return when (_uiState.value) {
            is NotesMainUIState -> {
                createNotesMainUIState()
            }

            is NotesEditorUIState -> {
                createNotesEditorUIState()
            }

            else -> {
                throw IllegalStateException()
            }
        }
    }

    fun saveEditorState(state: RichTextState) {
        this.editorState = state
    }

    private fun openDialog(
        title: Int,
        message: Int,
        onConfirm: () -> Unit,
        onCancel: () -> Unit,
        positiveBtn: Int = android.R.string.ok,
        negativeBtn: Int = android.R.string.cancel,
        hasCancelButton: Boolean = true
    ) {

        // TODO: Doest not look good, might be revisited later
        val newUiState = createNewUiState()

        newUiState.openDialog = true
        newUiState.dialogState = requestDialog(
            title = title,
            message = message,
            onConfirm = onConfirm,
            onCancel = onCancel,
            hasCancelButton = hasCancelButton,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn
        )

        _uiState.value = newUiState
    }

}