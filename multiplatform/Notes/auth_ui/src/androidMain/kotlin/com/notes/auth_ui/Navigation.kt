package com.notes.auth_ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.notes.auth_ui.screens.LoginUI
import com.notes.auth_ui.screens.OnboardingScreen
import com.notes.auth_ui.screens.RegisterUI
import com.notes.auth_ui.screens.VerificationEmailUI
import com.notes.notes_ui.getStartDestination
import com.notes.ui.AlertDialogUI
import com.notes.ui.getViewModel

fun NavGraphBuilder.authDestination(navController: NavController) {
    // Authentication graph
    navigation<Auth>(startDestination = Access) {
        composable<Access> { backStackEntry ->

            // Show login or register depending on UI state

            val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)
            val state = viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(false) {
                viewModel.onShowAuthUI()
            }

            if (state.value is AuthViewModel.LoginUIState) {
                val onSuccess = {
                    navController.popBackStack()
                    navController.navigate(getStartDestination())
                }

                val loginUIState = state.value as AuthViewModel.LoginUIState
                val hasProgress = loginUIState.showProgress

                LoginUI(
                    state = loginUIState,
                    onLogin = { viewModel.login(state = it, onSuccess = onSuccess) },
                    progress = hasProgress,
                )

                val context = LocalContext.current
                BackHandler(enabled = true) {
                    val activity = context as? Activity
                    activity?.moveTaskToBack(true)
                }
            }

            if (state.value is AuthViewModel.RegisterUIState) {
                val onSuccess = {
                    navController.navigate(EmailVerification)
                }

                RegisterUI(
                    state = state.value as AuthViewModel.RegisterUIState,
                    onRegister = {
                        viewModel.register(
                            state = it,
                            onSuccess = onSuccess,
                        )
                    },
                )
            }

            Dialog(viewModel)
        }

        composable<EmailVerification> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)
            val state = viewModel.uiState.collectAsStateWithLifecycle()

            val uiState = state.value as AuthViewModel.VerificationUIState

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                // Run a check to see if we got confirmation of user email
                viewModel.runConfirmationCheck(navController = navController)
            }

            VerificationEmailUI(
                onRetry = {
                    // Run a check to see if we got confirmation of user email
                    // and resend verification if we didn't
                    viewModel.runConfirmationCheck(true, navController)
                },
                verificationEmailSent = uiState.emailVerificationSent,
                title = uiState.title,
                subTitle = uiState.subtitle,
            )

            val context = LocalContext.current
            BackHandler(enabled = true) {
                val activity = context as? Activity
                activity?.moveTaskToBack(true)
            }
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
            dialogText = dialogState.value!!.subtitle,
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
