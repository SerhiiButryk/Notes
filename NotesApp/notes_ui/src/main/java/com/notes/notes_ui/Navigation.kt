package com.notes.notes_ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

fun NavGraphBuilder.mainContentDestination(navController: NavController) {

    // Create main app content graph
    navigation<Screen.MainContent>(startDestination = Screen.NotesPreview) {

        composable<Screen.NotesPreview> {
            NotesUI()
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