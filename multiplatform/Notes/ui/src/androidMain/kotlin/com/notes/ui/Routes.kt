package com.notes.ui

import api.data.isFirstLaunch
import kotlinx.serialization.Serializable

fun getStartDestination(): Screen = MainContent

fun getStartRoute(): Screen {
    if (isFirstLaunch()) {
        return OnBoardingScreen
    }
    return Auth
}

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.

@Serializable
open class Screen

@Serializable
object Auth : Screen()

@Serializable
object Access : Screen()

@Serializable
object OnBoardingScreen : Screen()

@Serializable
object EmailVerification : Screen()

@Serializable
object NotesPreview : Screen()

@Serializable
object MainContent : Screen()

@Serializable
object NotesSettings : Screen()

@Serializable
object NotesAccount : Screen()