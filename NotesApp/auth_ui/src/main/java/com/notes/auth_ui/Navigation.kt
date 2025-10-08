package com.notes.auth_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.notes.auth_ui.data.isUserRegistered
import com.notes.auth_ui.screens.LoginUI
import com.notes.auth_ui.screens.OnboardingScreen
import com.notes.auth_ui.screens.RegisterUI
import com.notes.ui.AlertDialogUI
import com.notes.ui.getViewModel

fun NavGraphBuilder.authDestination(navController: NavController) {

    // Authentication graph
    navigation<Auth>(startDestination = getAuthStartDestination()) {

        composable<Access> { backStackEntry ->

            // Show access screen

            val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)
            val state = viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(false) {
                if (isUserRegistered()) {
                    viewModel.onShowLoginUI()
                } else {
                    viewModel.onShowRegisterUI()
                }
            }

            if (state.value is AuthViewModel.LoginUIState) {

                val onSuccess = { navController.navigate(com.notes.notes_ui.getStartDestination()) }

                LoginUI(
                    state = state.value as AuthViewModel.LoginUIState,
                    onLogin = { viewModel.login(state = it, onSuccess = onSuccess) })
            }

            if (state.value is AuthViewModel.RegisterUIState) {

                val onSuccess = { viewModel.onShowLoginUI() }

                RegisterUI(
                    state = state.value as AuthViewModel.RegisterUIState,
                    onRegister = {
                        viewModel.register(
                            state = it,
                            onSuccess = onSuccess
                        )
                    })
            }

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
    composable<OnBoardingScreen> { backStackEntry ->
        OnboardingScreen {
            navController.navigate(route = Auth)
        }
    }
}
