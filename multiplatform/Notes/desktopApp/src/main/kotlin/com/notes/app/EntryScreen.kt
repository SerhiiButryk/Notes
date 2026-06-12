package com.notes.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import authDestination
import com.notes.auth_ui.AuthVM
import com.notes.ui.AccountInfoScreen
import com.notes.ui.LoginScreen
import com.notes.ui.PreviewScreen
import com.notes.ui.RegistrationScreen
import com.notes.ui.SettingsScreen
import com.notes.ui.createNavBackStack
import com.notes.ui.destinations
import notesMainDestination

@Composable
@Preview
fun EntryScreen() {

    val viewModel = viewModel { AuthVM() }

    // Should not be null at this point
    val startDestination = viewModel.startDestination!!

    val backstack = createNavBackStack(default = startDestination, elements = destinations)

    NavDisplay(
        backStack = backstack,
        entryProvider = entryProvider {

            authDestination(
                viewModel = viewModel,
                onNavLogin = {
                    backstack.clear()
                    backstack.add(PreviewScreen)
                },
                onNavRegister = {
                    backstack.clear()
                    backstack.add(LoginScreen)
                },
                onNavOnBoarding = {
                    backstack.add(RegistrationScreen)
                }
            )

            notesMainDestination(
                onSettingsClick = {
                    backstack.add(SettingsScreen)
                },
                onAccountClick = {
                    backstack.add(AccountInfoScreen)
                }
            )

        }
    )
}