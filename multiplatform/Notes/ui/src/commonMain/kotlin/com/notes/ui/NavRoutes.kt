package com.notes.ui

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object LoginScreen : NavKey

@Serializable
data object RegistrationScreen : NavKey

@Serializable
data object SettingsScreen : NavKey

@Serializable
data object AccountInfoScreen : NavKey

@Serializable
data object PreviewScreen : NavKey

@Serializable
data object OnBoardingNoteScreen : NavKey

@Serializable
data object LoadingNoteScreen : NavKey

private val startupDestinations = arrayOf(
    LoginScreen::class,
    RegistrationScreen::class,
    OnBoardingNoteScreen::class,
    LoadingNoteScreen::class,
)

val destinations =
    arrayOf(
        *startupDestinations,
        SettingsScreen::class,
        AccountInfoScreen::class,
        PreviewScreen::class,
    )

fun hasStartupDestination(backstack: NavBackStack<NavKey>): Boolean {
    backstack.forEach { dest ->
        val clazz = dest::class
        if (startupDestinations.contains(clazz)) {
            return true
        }
    }
    return false
}
