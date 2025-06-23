package com.notes.auth_ui

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.notes.ui.getViewModel
import kotlinx.serialization.Serializable

fun NavGraphBuilder.authDestination(navController: NavController) {

    navigation<Screen.Auth>(startDestination = getAuthStartDestination()) {

        composable<Screen.Login> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)
            val state = viewModel.loginState.collectAsStateWithLifecycle()

            LoginUI(state = state.value, onLogin = { viewModel.login(it) })
        }

        composable<Screen.Register> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)
            val state = viewModel.registerState.collectAsStateWithLifecycle()

            RegisterUI(state = state.value, onRegister = { viewModel.register(it) })
        }
    }
}

fun NavGraphBuilder.onboardingDestination(navController: NavController) {
    composable<Screen.OnBoardingScreen> { backStackEntry ->
        OnboardingScreen {
            navController.navigate(route = Screen.Auth)
        }
    }
}

fun getStartDestination(): Screen {
    val firstLaunch = true
    return if (firstLaunch)
        Screen.OnBoardingScreen
    else
        Screen.Auth
}

private fun getAuthStartDestination(): Screen {
    val registered = false
    return if (registered)
        Screen.Login
    else
        Screen.Register
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
    internal object Auth : Screen("auth")

    @Serializable
    internal object OnBoardingScreen : Screen("onboarding")
}