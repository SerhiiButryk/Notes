package com.notes.auth_ui

import com.notes.auth_ui.data.isFirstLaunch
import com.notes.ui.Screen
import kotlinx.serialization.Serializable

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.

@Serializable
internal object Auth : Screen()

@Serializable
internal object Access : Screen()

@Serializable
internal object OnBoardingScreen : Screen()

@Serializable
internal object EmailVerification : Screen()

fun getStartRoute(): Screen {
    if (isFirstLaunch()) {
        return OnBoardingScreen
    }
    return Auth
}
