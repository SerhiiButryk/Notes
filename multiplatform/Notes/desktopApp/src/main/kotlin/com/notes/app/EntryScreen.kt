package com.notes.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
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
import mainContentDestination

@Composable
fun EntryScreen() {
    val viewModel = viewModel { AuthVM() }
    val startDestination by remember { viewModel.startDestination }
    if (startDestination != null) {
        Navigation(viewModel = viewModel, startDestination = startDestination!!)
    } else {
        LoadingScreen()
    }
}

@Composable
@Preview
fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(100.dp),
                color = MaterialTheme.colorScheme.secondary,
                strokeWidth = 4.dp
            )
        }
    }
}

@Composable
fun Navigation(viewModel: AuthVM, startDestination: NavKey) {

    val backstack = createNavBackStack(default = startDestination, elements = destinations)

    val onBack = {
        val last = backstack.lastOrNull()
        if (last != null) {
            backstack.remove(last)
        }
    }

    NavDisplay(
        backStack = backstack,
        entryProvider =
            entryProvider {

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
                    },
                )

                mainContentDestination(
                    onSettingsClick = {
                        backstack.add(SettingsScreen)
                    },
                    onAccountClick = {
                        backstack.add(AccountInfoScreen)
                    },
                    onBack = onBack,
                )

            },
    )
}
