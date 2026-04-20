package com.notes.app.notes

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import authDestination
import com.notes.ui.AccountInfoScreen
import com.notes.ui.LoginScreen
import com.notes.ui.OnBoardingNoteScreen
import com.notes.ui.PreviewScreen
import com.notes.ui.RegistrationScreen
import com.notes.ui.SettingsScreen
import com.notes.ui.createNavBackStack
import com.notes.ui.destinations
import notesMainDestination

@Composable
@Preview
fun EntryScreen() {

    val backstack = createNavBackStack(default = OnBoardingNoteScreen, elements = destinations)

    NavDisplay(
        backStack = backstack,
        entryProvider = entryProvider {

            authDestination(
                onNavLogin = {
                    backstack.clear()
                    backstack.add(PreviewScreen)
                },
                onNavRegister = {
                    backstack.clear()
                    backstack.add(LoginScreen)
                },
                onNavContinue = {
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