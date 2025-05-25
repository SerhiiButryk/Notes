package com.notes.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.notes.auth_ui.Screen
import com.notes.auth_ui.authDestination

@Composable
fun EntryScreen(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Auth) {
        authDestination()
    }

}