/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.state_holders

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import com.serhii.apps.notes.R
import com.serhii.apps.notes.activities.NotesViewActivity
import com.serhii.apps.notes.activities.NotesViewActivity.Companion
import com.serhii.apps.notes.activities.SettingsActivity
import com.serhii.apps.notes.common.App
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
import java.io.FileNotFoundException
import java.io.OutputStream

/**
 *  View model for managing UI state and business logic of NotesViewActivity
 */
private const val TAG = "NotesViewModel"

class NotesViewModel : AppViewModel() {

    // UI state observable data
    private val _uiState = MutableStateFlow(BaseUIState())
    val uiState: StateFlow<BaseUIState> = _uiState

    private var cachedNotesData: List<NoteModel> = emptyList()
    private var editorState: RichTextState? = null

    ///////////////////////////// UI State class /////////////////////////////

    open class BaseUIState(
        var openDialog: Boolean = false,
        var dialogState: DialogUIState = DialogUIState()
    )

    class NotesMainUIState(val notes: List<NoteModel> = emptyList()) : BaseUIState()

    class NotesEditorUIState(
        var note: NoteModel = NoteModel(),
        var isExistingNote: Boolean = false
    ) : BaseUIState()

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

    override fun initViewModel(activity: Activity) {
        super.initViewModel(activity)
        // Init database
        UserNotesDatabase.init(activity.applicationContext)
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

            cachedNotesData = notesList

            // This might be called when we log in the first time
            // or do restore from settings or delete a note or close an note editor
            // In each case we should show Preview UI
            // So main state should be fine to set up here
            _uiState.value = createNotesMainUIState()

            Log.info(TAG, "reloadData()")
        }
    }

    fun deleteNote(uiState: NotesEditorUIState) {

        val deleteNote = {
            viewModelScope.launch(BACKGROUND_DISPATCHER) {
                val result = notesRepository.delete(uiState.note.id)
                if (result) {
                    // Reload data & closing editor screen
                    reloadData()
                }
                Log.info(TAG, "deleteNote() id = ${uiState.note.id} result = $result")
            }
        }

        openDialog(
            title = R.string.confirm_dialog_delete_note_title,
            message = R.string.confirm_dialog_delete_note_message,
            onConfirm = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                _uiState.value = createNewUiState(uiState)

                deleteNote()
            },
            onCancel = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                _uiState.value = createNewUiState(uiState)
            }
        )
    }

    fun saveNote(uiState: NotesEditorUIState) {

        if (editorState != null) {
            updateNote(uiState)
        } else {
            Log.info(TAG, "saveNote() failed editor is null")
            return
        }

        val saveNote = {
            viewModelScope.launch(BACKGROUND_DISPATCHER) {
                Log.info(TAG, "saveNote() id = ${uiState.note.id}")

                val id = uiState.note.id
                if (id.isEmpty()) {
                    val newId = notesRepository.add(uiState.note)

                    if (newId != -1) {
                        val note = notesRepository.get(newId.toString())
                        uiState.note = note

                        Log.info(TAG, "saveNote() success")
                    } else {
                        Log.info(TAG, "saveNote() failed")
                    }

                    return@launch
                }

                val result = notesRepository.update(id, uiState.note)
                if (result)
                    Log.info(TAG, "saveNote() updated note by id = $id")
                else
                    Log.info(TAG, "saveNote() failed to update note by id = $id")

            }
        }

        openDialog(
            title = R.string.confirm_dialog_save_note_title,
            message = R.string.confirm_dialog_save_note_message,
            onConfirm = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                _uiState.value = createNewUiState(uiState)

                saveNote()
            },
            onCancel = {
                // TODO: Doest not look good, might be revisited later
                // A hack to close dialog
                _uiState.value = createNewUiState(uiState)
            }
        )
    }

    private fun updateNote(uiState: NotesEditorUIState) {
        val textNote = editorState!!.toText()
        uiState.note.plainText = textNote
    }

    override fun backupNote(uiState: NotesEditorUIState) {

        if (editorState != null) {
            updateNote(uiState)
        } else {
            Log.error(TAG, "backupNote() failed editor is null")
            return
        }

        super.backupNote(uiState)
    }

    fun openNoteEditorUI(noteModel: NoteModel? = null) {
        if (noteModel == null) {
            _uiState.value = NotesEditorUIState()
        } else {
            _uiState.value = NotesEditorUIState(noteModel, true)
        }
    }

    fun navigateBack(): Boolean {
        val currentUIState = _uiState.value

        if (currentUIState is NotesEditorUIState) {
            // Go to previous screen
            reloadData()
            return true
        }

        return false
    }

    fun extractNote(context: Context, intent: Intent) {
        val noteId = (uiState.value as NotesEditorUIState).note.id

        if (noteId.isEmpty()) {
            Log.error(TAG, "extractNote() didn't get note id, return")
            return
        }

        val outputStream: OutputStream? = try {
            context.contentResolver.openOutputStream(intent.data!!)
        } catch (e: FileNotFoundException) {
            Log.error(TAG, "extractNote() failed to get output stream, error: $e")
            e.printStackTrace()
            return
        }

        // Start an extract
        viewModelScope.launch(App.BACKGROUND_DISPATCHER) {
            val note = UserNotesDatabase.getRecord(noteId)
            BackupManager.extractNotes(outputStream, listOf(note)) { result ->
                showStatusMessage(context, result)
            }
        }
    }

    fun openSettings(activity: Activity) {
        activity.startActivity(Intent(activity, SettingsActivity::class.java))
    }

    private fun createNotesMainUIState(): NotesMainUIState {
        return NotesMainUIState(cachedNotesData)
    }

    private fun createNotesEditorUIState(copyUiState: NotesEditorUIState): NotesEditorUIState {
        return NotesEditorUIState(copyUiState.note)
    }

    private fun createNewUiState(copyUiState: BaseUIState): BaseUIState {
        return when (_uiState.value) {
            is NotesMainUIState -> {
                createNotesMainUIState()
            }

            is NotesEditorUIState -> {
                createNotesEditorUIState(copyUiState as NotesEditorUIState)
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
        val newUiState = createNewUiState(uiState.value)

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