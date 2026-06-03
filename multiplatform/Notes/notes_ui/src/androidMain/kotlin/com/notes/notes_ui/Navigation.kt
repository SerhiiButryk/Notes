package com.notes.notes_ui

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import api.Platform
import api.data.Image
import api.data.Notes
import com.notes.notes_ui.data.UiEvent
import com.notes.notes_ui.editor.EditorCommand
import com.notes.ui.Access
import com.notes.ui.Auth
import com.notes.ui.LoadingDialog
import com.notes.ui.MainContent
import com.notes.ui.MediaPreview
import com.notes.ui.NotesAccount
import com.notes.ui.NotesPreview
import com.notes.ui.NotesSettings
import com.notes.ui.getViewModel
import com.notes.ui.isTabletOrFoldableExpanded
import kotlinx.coroutines.flow.Flow

fun NavGraphBuilder.mainContentDestination(navController: NavController) {

    val onBackClick: () -> Unit = { navController.popBackStack() }

    // Main app content graph
    navigation<MainContent>(startDestination = NotesPreview()) {
        composable<NotesPreview> { backStackEntry ->

            val context = LocalContext.current
            val factory = NotesViewModel.getFactory(context)

            val viewModel = backStackEntry.getViewModel<NotesViewModel>(navController, factory)

            val noteList by viewModel.notesState.collectAsStateWithLifecycle()
            val note by viewModel.noteState.collectAsStateWithLifecycle()

            val onSelectAction: suspend (Notes) -> Unit =
                { note -> viewModel.onSelectAction(note) }

            val onAddAction: suspend () -> Unit =
                { viewModel.onAddAction() }

            val onNavigatedBack: suspend () -> Unit = { viewModel.onNavigatedBack() }

            val sendEditorCommand: (EditorCommand) -> Unit =
                { viewModel.sendEditorCommand(it) }

            val getEvents: suspend () -> Flow<UiEvent> = { viewModel.events }

            val onSettingsClicked = { navController.navigate(NotesSettings()) }

            val toolsPaneItems = viewModel.richTools

            val onBackButtonClicked: () -> Unit = {
                // Back stack is empty at this point so we use activity context
                val activity = context as? Activity
                activity?.moveTaskToBack(true)
            }

            BackHandler(enabled = true) {
                onBackButtonClicked()
            }

            val sizeClass = currentWindowAdaptiveInfo().windowSizeClass

            val attachments by viewModel.attachments.collectAsStateWithLifecycle()

            val onOpenPreview: (Image) -> Unit = {
                val uri = it.location as Uri
                navController.navigate(MediaPreview(uri.toString(), it.name))
            }

            val onDelete: (Image) -> Unit = {
                viewModel.onDelete(it)
            }

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                viewModel.onAttachments(uri, context)
            }

            val onAttachFile = {
                viewModel.onAttachFile(launcher)
            }

            NotesUI(
                notes = noteList,
                toolsPaneItems = toolsPaneItems,
                onAddAction = onAddAction,
                note = note,
                onSelectAction = onSelectAction,
                onNavigatedBack = onNavigatedBack,
                onTextChanged = sendEditorCommand,
                getEvents = getEvents,
                onSettingsClick = onSettingsClicked,
                onBackClick = onBackButtonClicked,
                showNavRail = isTabletOrFoldableExpanded(sizeClass),
                isPhoneSize = !isTabletOrFoldableExpanded(sizeClass),
                attachments = attachments,
                onOpenPreview = onOpenPreview,
                onDelete = onDelete,
                onAttachFile = onAttachFile,
            )

            Dialog(viewModel)
        }

        composable<NotesSettings> { backStackEntry ->

            val context = LocalContext.current

            val viewModel = backStackEntry.getViewModel<SettingsViewModel>(navController)

            val onAccountSelected = { navController.navigate(NotesAccount()) }
            val onExport: (uri: Uri?, context: Context) -> Unit = { uri, context ->
                viewModel.onExport(uri, context)
            }

            val onPasswordUpdateClick = {
                navController.navigate(Access(showChangePasswordUI = true))
            }

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocumentTree()
            ) { result ->
                onExport(result, context)
            }

            SettingsUI(
                onBackClick = onBackClick,
                onAccountClick = onAccountSelected,
                onExportClick = {
                    // Ask User to select a folder for notes export
                    launcher.launch(null)
                },
                onPasswordUpdateClick = onPasswordUpdateClick
            )
        }

        composable<NotesAccount> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<SettingsViewModel>(navController)

            val onSignOut = {
                viewModel.singOut {
                    navController.navigate(Auth())
                }
            }

            val accountInfo by viewModel.accountInfo.collectAsStateWithLifecycle()

            val activity = LocalActivity.current

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                val result = result.resultCode == Activity.RESULT_OK
                Platform().logger.logi("AccountUI::activity result = $result")
                viewModel.updateAccountInfo()
            }

            val onSuccess = { sender: IntentSenderRequest ->
                launcher.launch(sender)
            }

            val onGrantPermissionClick: () -> Unit =
                { viewModel.requestPermissions(activity, onSuccess) }

            AccountUI(
                onBackClick = onBackClick,
                onGrantPermissionClick = onGrantPermissionClick,
                onSignOut = onSignOut,
                accountInfo = accountInfo
            )

        }

        composable<MediaPreview> { backStackEntry ->

            val args = backStackEntry.toRoute<MediaPreview>()

            PreviewScreen(
                uri = args.uri.toUri(),
                onBackClick = onBackClick,
                title = args.name,
            )

            BackHandler(enabled = true) {
                onBackClick()
            }

        }

    }
}

@Composable
private fun Dialog(viewModel: NotesViewModel) {
    val dialogState = viewModel.dialogState.collectAsStateWithLifecycle()
    LoadingDialog(dialogState.value.show)
}
