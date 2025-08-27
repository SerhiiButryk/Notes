package com.notes.notes_ui

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.notes.ui.getViewModel
import kotlinx.serialization.Serializable

fun NavGraphBuilder.mainContentDestination(navController: NavController) {

    // Main app content graph
    navigation<Screen.MainContent>(startDestination = Screen.NotesPreview) {

        composable<Screen.NotesPreview> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<NotesViewModel>(navController)
            val noteList by viewModel.notesState.collectAsStateWithLifecycle()

            NotesUI(notes = noteList)
        }

    }

}

fun getStartDestination() : Screen = Screen.NotesPreview

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.
@Serializable
sealed class Screen(val route: String) {

    @Serializable
    internal object NotesPreview : Screen("notes_preview")

    @Serializable
    internal object MainContent : Screen("main_content")

}