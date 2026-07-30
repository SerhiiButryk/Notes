package com.notes.notes_ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import api.Platform
import api.data.Attachments
import api.data.Notes
import api.data.NotesCollection
import api.data.UserFile
import api.repo.RepoCallback
import api.repo.Repository
import com.notes.notes_ui.data.UiEvent
import com.notes.notes_ui.data.getToolsList
import com.notes.notes_ui.editor.EditorCommand
import com.notes.ui.model.BaseAppVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    appRepository: Repository = Platform().appRepo,
    // For test support
    scopeOverride: CoroutineScope? = null
) : BaseAppVM(scopeOverride), RepoCallback {

    companion object {
        fun getFactory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    NotesViewModel()
                }
            }
        }
    }

    private val interactor: Interactor = Interactor(appRepository, this)

    // A state to hold all the notes
    val notesState: StateFlow<NotesCollection> = interactor
        .getNotes()
        .map { NotesCollection(it) }
        .stateIn(
            scope = scope,
            started = WhileSubscribed(stopTimeoutMillis = 5000),
            NotesCollection(),
        )

    // A state to hold the note which is open in Editor
    private val _noteState = MutableStateFlow(Notes.AbsentNote())
    val noteState = _noteState.asStateFlow()

    val richTools = getToolsList(interactor)

    private val uiEvents = Channel<UiEvent>(capacity = Channel.BUFFERED)
    val events = uiEvents.receiveAsFlow()

    val attachments = interactor
        .getAttachments()
        .stateIn(
            scope = scope,
            started = WhileSubscribed(stopTimeoutMillis = 5000),
            Attachments(),
        )

    override fun onCleared() {
        super.onCleared()
        interactor.onClear()
    }

    // User selected a note from list ui
    suspend fun onSelectAction(note: Notes) {
        val found = notesState.value.collection.firstOrNull { note.id == it.id }
        if (found == null) {
            val note = interactor.getNotes(note.id).first()!!
            _noteState.emit(note)
        } else {
            _noteState.emit(found)
        }
        interactor.onNoteOpened()
    }

    // User clicked on '+' button in ui to create an empty note
    suspend fun onAddAction() {
        _noteState.emit(Notes.NewNote())
        interactor.onNoteOpened()
    }

    override fun onNoteAdded(id: Long) { // Note has been updated in repository
        scope.launch {
            // Select updated note to make sure that ui has the latest state
            onSelectAction(Notes(id = id))
        }
    }

    override fun onNoteDeleted(id: Long) { // Note has been deleted in repository
        scope.launch {
            uiEvents.send(UiEvent.NavigateToListPane())
        }
    }

    override fun onNoteNavigateBack() {
        scope.launch {
            uiEvents.send(UiEvent.NavigateToListPane())
        }
    }

    suspend fun onNavigatedBack() {
        // Emitting 'AbsentNote' to show 'Select an item' message in
        // the editor to inform user that editor is inactive
        // he/she should select a note from list ui
        _noteState.emit(Notes.AbsentNote())
    }

    fun sendEditorCommand(command: EditorCommand) {
        interactor.sendEditorCommand(command)
    }

    fun onShowFilePicker(launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
        // Show dialog
        scope.launch {
            showLoadingDialog()
        }
        // Ask User to select an image
        launcher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    fun onAttachments(uri: Uri?, context: Context) {
        scope.launch {
            dismissLoadingDialog()
        }
        if (uri == null) return
        scope.launch {
            val openNoteId = _noteState.value.id
            val result = interactor.onAttachments(uri, openNoteId, context)
            if (!result) {
                val title = "An error"
                val subtitle = "Sorry, failed to add the file. Please, try again."
                showDialog(title = title, subtitle = subtitle)
            }
        }
    }

    fun onDeleteAttachment(file: UserFile) {
        scope.launch {
            val result = interactor.onDeleteAttachment(file)
            if (!result) {
                val title = "An error"
                val subtitle = "Sorry, failed to delete. Please, try again."
                showDialog(title = title, subtitle = subtitle)
            }
        }
    }

}
