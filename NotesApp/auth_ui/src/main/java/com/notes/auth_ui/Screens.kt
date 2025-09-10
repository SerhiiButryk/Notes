package com.notes.auth_ui

import com.notes.auth_ui.data.isFirstLaunch
import com.notes.auth_ui.data.isUserRegistered
import com.notes.ui.Screen
import kotlinx.serialization.Serializable

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.

@Serializable
internal object Auth : Screen("auth")

@Serializable
internal object Access : Screen("access")

@Serializable
internal object OnBoardingScreen : Screen("onboarding")

internal fun getStartRoute(): Screen {
    if (isFirstLaunch())
        return OnBoardingScreen
    return getAuthStartDestination()
}

internal fun getAuthStartDestination(): Screen {
    return Access
}