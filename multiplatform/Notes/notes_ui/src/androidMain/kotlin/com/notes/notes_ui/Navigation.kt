package com.notes.notes_ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import api.PlatformAPIs.logger
import api.data.Notes
import com.notes.notes_ui.editor.EditorCommand
import com.notes.notes_ui.screens.AccountUI
import com.notes.notes_ui.screens.NotesUI
import com.notes.notes_ui.screens.SettingsUI
import com.notes.ui.Screen
import com.notes.ui.getViewModel
import com.notes.ui.navAndPopUpCurrent
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

fun NavGraphBuilder.mainContentDestination(navController: NavController) {
    // Main app content graph
    navigation<MainContent>(startDestination = NotesPreview) {
        composable<NotesPreview> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<NotesViewModel>(navController)

            val noteList by viewModel.notesState.collectAsStateWithLifecycle()
            val note by viewModel.noteState.collectAsStateWithLifecycle()

            val onSelectAction: suspend (Notes) -> Unit =
                { note -> viewModel.onSelectAction(note) }

            val onAddAction: suspend () -> Unit =
                { viewModel.onAddAction() }

            val onNavigatedBack: suspend () -> Unit = { viewModel.onNavigatedBack() }

            val sendEditorCommand: (EditorCommand) -> Unit =
                { viewModel.sendEditorCommand(it) }

            val getEvents: suspend () -> Flow<NotesViewModel.UiEvent> = { viewModel.events }

            val onSettingsClicked = { navController.navigate(NotesSettings) }

            val toolsPaneItems = viewModel.richTools

            val context = LocalContext.current
            val onBackButtonClicked: () -> Unit = {
                // Back stack is empty at this point so we use activity context
                val activity = context as? Activity
                activity?.moveTaskToBack(true)
            }

            BackHandler(enabled = true) {
                onBackButtonClicked()
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
                onBackClick = onBackButtonClicked
            )
        }

        composable<NotesSettings> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<SettingsViewModel>(navController)

            val onBackClicked: () -> Unit = { navController.navAndPopUpCurrent(NotesPreview) }

            val onAccountSelected = { navController.navigate(NotesAccount) }

            SettingsUI(onBackClick = onBackClicked, onAccountClick = onAccountSelected)
        }

        composable<NotesAccount> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<SettingsViewModel>(navController)

            val onBackClick: () -> Unit = { navController.navAndPopUpCurrent(NotesSettings) }

            val onSignOut = { viewModel.singOut() }

            val accountInfo by viewModel.accountInfo.collectAsStateWithLifecycle()

            val activity = LocalActivity.current

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                val result = result.resultCode == Activity.RESULT_OK
                logger.logi("AccountUI::activity result = $result")
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
    }
}

fun getStartDestination(): Screen = MainContent

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.

@Serializable
object NotesPreview : Screen()

@Serializable
object MainContent : Screen()

@Serializable
internal object NotesSettings : Screen()

@Serializable
internal object NotesAccount : Screen()
