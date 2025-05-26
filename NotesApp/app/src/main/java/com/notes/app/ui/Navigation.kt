package com.notes.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.notes.auth_ui.authDestination
import com.notes.auth_ui.getStartDestination

@Composable
fun EntryScreen(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = getStartDestination()) {
        authDestination(navController = navController)
    }

}