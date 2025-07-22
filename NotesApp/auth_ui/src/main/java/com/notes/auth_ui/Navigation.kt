package com.notes.auth_ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.notes.auth_ui.data.isFirstLaunch
import com.notes.auth_ui.data.isUserRegistered
import com.notes.ui.AlertDialogUI
import com.notes.ui.getViewModel

fun NavGraphBuilder.authDestination(navController: NavController) {

    // Create app authentication graph
    navigation<Screen.Auth>(startDestination = getAuthStartDestination()) {

        composable<Screen.Login> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)
            val state = viewModel.loginState.collectAsStateWithLifecycle()

            val onSuccess = { navController.navigate(com.notes.notes_ui.getStartDestination()) }

            LoginUI(
                state = state.value,
                onLogin = { viewModel.login(loginUIState = it, onSuccess = onSuccess) })

            Dialog(viewModel)

        }

        composable<Screen.Register> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)
            val state = viewModel.registerState.collectAsStateWithLifecycle()

            val onSuccess = { navController.navigate(Screen.Login) }

            RegisterUI(
                state = state.value,
                onRegister = { viewModel.register(registerUIState = it, onSuccess = onSuccess) })

            Dialog(viewModel)

        }

    }
}

@Composable
private fun Dialog(viewModel: AuthViewModel) {

    val dialogState = viewModel.dialogState.collectAsStateWithLifecycle()

    if (dialogState.value != null) {
        AlertDialogUI(
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirmation = { viewModel.dismissDialog() },
            dialogTitle = dialogState.value!!.title,
            dialogText = dialogState.value!!.subtitle
        )
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
    return if (isFirstLaunch())
        Screen.OnBoardingScreen
    else
        Screen.Auth
}

private fun getAuthStartDestination(): Screen {
    return if (isUserRegistered())
        Screen.Login
    else
        Screen.Register
}