package com.notes.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import api.PlatformAPIs.logger
import kotlinx.serialization.Serializable

/**
 * Define new screen using this base class.
 */
@Serializable
open class Screen

// To navigate back safely
fun NavController.navAndPopUpCurrent(destination: Any) {
    val route = this.currentDestination?.route
    navigate(destination) {
        // Pop up to the current destination of the graph to
        // avoid having a stack of this screens
        if (route != null) {
            popUpTo(route) {
                inclusive = true
            }
        }
        // Avoid multiple copies of the same destination
        launchSingleTop = true
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun NavControllerStateDebug(navController: NavController) {
    val backStack by navController.currentBackStack.collectAsState()
    LaunchedEffect(backStack) {
        navController.graph.forEach { destination ->
            logger.logi("Destination: ${destination.label} - ${destination.route}")
        }
        val stackRoutes = backStack.map { entry -> entry.destination.route }
        logger.logi("NavigationBackStack BackStack: '$stackRoutes'")
    }
}
