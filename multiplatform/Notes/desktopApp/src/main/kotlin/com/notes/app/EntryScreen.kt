package com.notes.app

import Menu
import MenuState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import authDestination
import com.notes.auth_ui.AuthVM
import com.notes.ui.LoadingNoteScreen
import com.notes.ui.LoginScreen
import com.notes.ui.OnBoardingNoteScreen
import com.notes.ui.PreviewScreen
import com.notes.ui.RegistrationScreen
import com.notes.ui.SettingsScreen
import com.notes.ui.createNavBackStack
import com.notes.ui.destinations
import com.notes.ui.hasStartupDestination
import mainContentDestination

@Composable
fun FrameWindowScope.EntryScreen(
    appScope: ApplicationScope,
    onThemeChange: () -> Unit,
) {
    val menuState = remember { mutableStateOf(MenuState()) }
    Menu(appScope = appScope, menuState = menuState)
    Navigation(
        menuState = menuState,
        onThemeChange = onThemeChange,
    )
}

@Composable
private fun Navigation(
    menuState: MutableState<MenuState>,
    onThemeChange: () -> Unit,
) {

    val viewModel = viewModel { AuthVM() }

    val startScreenState = viewModel.startDestination

    val backstack = createNavBackStack(default = startScreenState.value, elements = destinations)

    val onBack = {
        backstack.removeLast()
        Unit
    }

    // Handle menu state changes

    LaunchedEffect(menuState) {
        snapshotFlow { menuState.value }
            .collect { menu ->
                if (menu.isShown) {
                    when (menu.activeMenuItem) {
                        MenuState.SETTINGS_MENU -> {
                            val canShow = !backstack.contains(SettingsScreen)
                                    && !hasStartupDestination(backstack)
                            if (canShow)
                                backstack.add(SettingsScreen)
                        }
                    }
                    menuState.value =
                        menuState.value.copy(isShown = false, activeMenuItem = MenuState.NONE)
                }
            }
    }

    // Handle start screen state changes

    LaunchedEffect(startScreenState) {
        snapshotFlow { startScreenState.value }
            .collect { screen ->
                backstack.removeLast()
                backstack.add(screen)
            }
    }

    // Main navigation graph

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
                    onBack = onBack,
                    onNavToAuth = {
                        backstack.clear()
                        backstack.add(LoginScreen)
                    },
                    onThemeChange = onThemeChange,
                )

                entry(LoadingNoteScreen) {
                    LoadingScreen()
                }

            },
    )
}

@Composable
@Preview
private fun LoadingScreen() {
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