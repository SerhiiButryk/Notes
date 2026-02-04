package com.notes.notes_ui

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import api.data.Notes
import com.notes.notes_ui.editor.EditorCommand
import com.notes.notes_ui.screens.AccountUI
import com.notes.notes_ui.screens.NotesUI
import com.notes.notes_ui.screens.SettingsUI
import com.notes.ui.Screen
import com.notes.ui.getViewModel
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
            LaunchedEffect(false) {
                viewModel.init(context)
            }

            val onBackButtonClicked: () -> Unit = {
                // Back stack is empty at this point so we use activity context
                val activity = context as? Activity
                activity?.moveTaskToBack(true)
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

            val onBackClicked: () -> Unit = { navController.popBackStack() }
            val onAccountSelected = { navController.navigate(NotesAccount) }

            SettingsUI(onBackClick = onBackClicked, onAccountClick = onAccountSelected)
        }

        composable<NotesAccount> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<SettingsViewModel>(navController)

            val onBackClicked: () -> Unit = { navController.popBackStack() }
            val onSignOut = { viewModel.singOut() }
            val requestPermissions: (context: Any?, launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) -> Unit =
                { ctx, launcher -> viewModel.requestPermissions(ctx, launcher) }

            val onActivityResult: () -> Unit = { viewModel.onActivityResult() }

            val accountInfo by viewModel.accountInfo.collectAsStateWithLifecycle()

            AccountUI(
                onBackClick = onBackClicked,
                requestPermissions = requestPermissions,
                onSignOut = onSignOut,
                onActivityResult = onActivityResult,
                accountInfo = accountInfo
            )

        }
    }
}

fun getStartDestination(): Screen = MainContent

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.

@Serializable
internal object NotesPreview : Screen("notes_preview")

@Serializable
object MainContent : Screen("main_content")

@Serializable
internal object NotesSettings : Screen("settings")

@Serializable
internal object NotesAccount : Screen("account")
