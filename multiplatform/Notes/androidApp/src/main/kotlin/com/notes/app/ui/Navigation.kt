package com.notes.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.notes.auth_ui.authDestination
import com.notes.auth_ui.onboardingDestination
import com.notes.notes_ui.mainContentDestination
import com.notes.ui.getStartRoute

@Composable
fun EntryScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = getStartRoute()) {
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
