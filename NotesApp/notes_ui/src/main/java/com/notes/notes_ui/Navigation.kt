package com.notes.notes_ui

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.notes.notes_ui.screens.NotesUI
import com.notes.ui.Screen
import com.notes.ui.getViewModel
import kotlinx.serialization.Serializable

fun NavGraphBuilder.mainContentDestination(navController: NavController) {

    // Main app content graph
    navigation<MainContent>(startDestination = NotesPreview) {

        composable<NotesPreview> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<NotesViewModel>(navController)
            val noteList by viewModel.notesState.collectAsStateWithLifecycle()

            val toolsPaneItems = viewModel.richTools

            NotesUI(notes = noteList, toolsPaneItems = toolsPaneItems)
        }

    }

}

fun getStartDestination() : Screen = MainContent

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.

@Serializable
internal object NotesPreview : Screen("notes_preview")

@Serializable
object MainContent : Screen("main_content")