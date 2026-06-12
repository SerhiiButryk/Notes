package com.notes.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import api.data.isFirstLaunch
import com.notes.auth_ui.authDestination
import com.notes.auth_ui.onboardingDestination
import com.notes.notes_ui.mainContentDestination
import com.notes.ui.Auth
import com.notes.ui.OnBoardingScreen

@Composable
fun EntryScreen(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val startDestination =  if (isFirstLaunch()) OnBoardingScreen() else Auth()

    NavHost(navController = navController, startDestination = startDestination) {
        onboardingDestination(navController = navController)

        authDestination(navController = navController)

        mainContentDestination(navController = navController)
    }
}

// Preview example for tests
//@Composable
//@Preview(
//    name = "Dark Mode",
//    showBackground = true,
//    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
//)
//@Preview(name = "Light mode")
//@Preview(
//    name = "Dark Mode landscape",
//    showBackground = true,
//    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
//    device = "spec:parent=pixel_5,orientation=landscape"
//)
//@Preview(name = "Light mode landscape", device = "spec:parent=pixel_5,orientation=landscape")
//fun TestOnly() {
//    AppTheme {
//        AccountUI({}, {}, {}, AccountInfo())
//    }
//}
