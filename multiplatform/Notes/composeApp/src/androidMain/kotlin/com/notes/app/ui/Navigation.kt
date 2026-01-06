package com.notes.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.notes.auth_ui.authDestination
import com.notes.auth_ui.getStartRoute
import com.notes.auth_ui.onboardingDestination
import com.notes.notes_ui.mainContentDestination

@Composable
fun EntryScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = getStartRoute()) {
        onboardingDestination(navController = navController)

        authDestination(navController = navController)

        mainContentDestination(navController = navController)
    }
}
