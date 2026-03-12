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
import api.data.UserFile
import api.repo.RepoCallback
import api.repo.Repository
import com.notes.notes_ui.components.ViewModelCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    appRepository: Repository = Platform().appRepo,
    // For test support
    scopeOverride: CoroutineScope? = null,
) : NotesVMBase(scopeOverride, appRepository), RepoCallback {

    companion object {
        fun getFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    NotesViewModel()
                }
            }
    }

    // Attachments of the user notes
    val attachments =
        interactor
            .getAttachments()
            .stateIn(
                scope = scope,
                started = WhileSubscribed(stopTimeoutMillis = 5000),
                Attachments(),
            )

    override fun onEditorNavBack() {
        scope.launch {
            viewModelCommand.send(ViewModelCommand.NavigateToListPane())
        }
    }

    fun onShowFilePicker(launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
        // Show dialog
        scope.launch {
            showLoadingDialog()
        }
        // Ask User to select an image
        launcher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly,
            ),
        )
    }

    fun onAttachments(
        uri: Uri?,
        context: Context,
    ) {
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
