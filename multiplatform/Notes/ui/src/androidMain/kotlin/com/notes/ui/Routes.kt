package com.notes.ui

import api.data.isFirstLaunch
import kotlinx.serialization.Serializable

fun getStartDestination(): Screen = MainContent()

fun getStartRoute(): Screen {
    if (isFirstLaunch()) {
        return OnBoardingScreen()
    }
    return Auth()
}

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.

@Serializable
open class Screen

@Serializable
class Auth : Screen()

@Serializable
class Access(
    val showChangePasswordUI: Boolean = false,
    val forceLoginUI: Boolean = false
) : Screen()

@Serializable
class OnBoardingScreen : Screen()

@Serializable
class EmailVerification : Screen()

@Serializable
class NotesPreview : Screen()

@Serializable
class MainContent : Screen()

@Serializable
class NotesSettings : Screen()

@Serializable
class NotesAccount : Screen()