package com.notes.auth_ui

import kotlinx.serialization.Serializable

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.
@Serializable
sealed class Screen(val route: String) {

    @Serializable
    internal object Login : Screen("login")

    @Serializable
    internal object Register : Screen("register")

    @Serializable
    internal object Auth : Screen("auth")

    @Serializable
    internal object OnBoardingScreen : Screen("onboarding")
}