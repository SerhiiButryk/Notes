package com.notes.auth_ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.notes.auth_ui.data.LoginUIState
import com.notes.auth_ui.data.RegisterUIState
import com.notes.auth_ui.data.VerificationUIState
import com.notes.auth_ui.ui.LoginUI
import com.notes.auth_ui.ui.OnboardingScreen
import com.notes.auth_ui.ui.RegisterUI
import com.notes.auth_ui.ui.VerificationEmailUI
import com.notes.ui.Access
import com.notes.ui.AlertDialogUI
import com.notes.ui.Auth
import com.notes.ui.EmailVerification
import com.notes.ui.NotesSettings
import com.notes.ui.OnBoardingScreen
import com.notes.ui.SettingsScreen
import com.notes.ui.getStartDestination
import com.notes.ui.getViewModel
import com.notes.ui.navAndPopUpCurrent

fun NavGraphBuilder.authDestination(navController: NavController) {
    // Authentication graph
    navigation<Auth>(startDestination = Access()) {
        composable<Access> { backStackEntry ->

            // Show login or register depending on UI state

            val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)
            val state = viewModel.uiState.collectAsStateWithLifecycle()

            // Arguments
            val args: Access = backStackEntry.toRoute()

            val title = state.value.title
            val subTitle = state.value.subtitle

            LaunchedEffect(args.forceLoginUI) {
                viewModel.onShowAccessUI(args)
            }

            if (state.value is LoginUIState) {
                val onSuccess = {
                    navController.navAndPopUpCurrent(getStartDestination())
                }

                val loginUIState = state.value as LoginUIState

                val activityContext: Any? = LocalActivity.current

                LoginUI(
                    state = loginUIState,
                    onLogin = {
                        viewModel.login(state = it, onSuccess = onSuccess, context = activityContext, args = args)
                    },
                    title = title,
                    subTitle = subTitle
                )

                val context = LocalContext.current
                if (!args.showChangePasswordUI) {
                    BackHandler(enabled = true) {
                        val activity = context as? Activity
                        activity?.moveTaskToBack(true)
                    }
                }
            }

            if (state.value is RegisterUIState) {


                val onLogin = {
                    // Go to login screen
                    navController.navigate(Access(forceLoginUI = true))
                }

                val onRegister: (RegisterUIState) -> Unit = {
                    if (args.showChangePasswordUI) {

                        val onSuccess = {
                            navController.popBackStack()
                            Unit
                        }

                        viewModel.changePassword(state = it, onSuccess = onSuccess)
                    } else {

                        val onSuccess = {
                            navController.navigate(EmailVerification())
                        }

                        viewModel.register(
                            state = it,
                            onSuccess = onSuccess,
                        )
                    }
                }

                RegisterUI(
                    state = state.value as RegisterUIState,
                    onRegister = onRegister,
                    onLogin = if (args.showChangePasswordUI) null else onLogin,
                    title = title,
                    subTitle = subTitle
                )
            }

            Dialog(viewModel)
        }

        composable<EmailVerification> { backStackEntry ->

            val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)
            val state = viewModel.uiState.collectAsStateWithLifecycle()

            val uiState = state.value as VerificationUIState

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
    val dialogValue = dialogState.value
    if (dialogValue != null) {
        AlertDialogUI(
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirmation = {
                viewModel.dismissDialog()
                dialogValue.onConfirm?.invoke()
            },
            dialogTitle = dialogValue.title,
            dialogText = dialogValue.subtitle,
        )
    }
}

fun NavGraphBuilder.onboardingDestination(navController: NavController) {
    composable<OnBoardingScreen> { backStackEntry ->

        val viewModel = backStackEntry.getViewModel<AuthViewModel>(navController)

        LaunchedEffect(false) {
            viewModel.onShowOnBoardingUI(navController)
        }

        OnboardingScreen {
            navController.navigate(Auth())
        }
    }
}
