package com.notes.notes_ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import api.data.Notes
import com.notes.notes_ui.screens.NotesUI
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

            val toolsPaneItems = viewModel.richTools

            val context = LocalContext.current
            LaunchedEffect(false) {
                viewModel.init(context)
            }

            NotesUI(
                notes = noteList,
                toolsPaneItems = toolsPaneItems,
                onAddAction = onAddAction,
                note = note,
                onSelectAction = onSelectAction,
                onNavigatedBack = onNavigatedBack,
                onTextChanged = sendEditorCommand,
                getEvents = getEvents
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
