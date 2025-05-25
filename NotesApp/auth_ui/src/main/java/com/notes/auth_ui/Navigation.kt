package com.notes.auth_ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

fun NavGraphBuilder.authDestination() {
    navigation<Screen.Auth>(startDestination = Screen.Login) {
        composable<Screen.Login> {
            LoginUI()
        }

        composable<Screen.Register> {
            RegisterUI()
        }
    }
}

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.
@Serializable
sealed class Screen(val route: String) {
    @Serializable
    internal object Login : Screen("login")

    @Serializable
    internal object Register : Screen("register")

    @Serializable
    object Auth : Screen("auth")
}