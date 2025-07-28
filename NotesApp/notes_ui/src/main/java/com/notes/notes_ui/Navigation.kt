package com.notes.notes_ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.notes.notes_ui.richeditor.NotesEditorUI
import kotlinx.serialization.Serializable

fun NavGraphBuilder.mainContentDestination(navController: NavController) {

    // Main app content graph
    navigation<Screen.MainContent>(startDestination = Screen.NotesPreview) {

        composable<Screen.NotesPreview> {

            val addButtonAction: () -> Unit = {
                navController.navigate(Screen.NotesEditor)
            }

            NotesUI(addButtonAction = addButtonAction)
        }

        composable<Screen.NotesEditor> {
            NotesEditorUI()
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

    @Serializable
    internal object NotesEditor : Screen("editor")

    @Serializable
    internal object Settings : Screen("settings")

}