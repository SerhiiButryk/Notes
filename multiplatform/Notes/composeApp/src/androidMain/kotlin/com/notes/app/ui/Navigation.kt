package com.notes.app.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.notes.auth_ui.authDestination
import com.notes.auth_ui.getStartRoute
import com.notes.auth_ui.onboardingDestination
import com.notes.notes_ui.mainContentDestination
import com.notes.notes_ui.screens.AccountUI
import com.notes.notes_ui.screens.SettingsUI
import com.notes.ui.theme.AppTheme

@Composable
fun EntryScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = getStartRoute()) {
        onboardingDestination(navController = navController)

        authDestination(navController = navController)

        mainContentDestination(navController = navController)
    }
}

// Preview in a SHARED KMP module doesn't work, this is a workaround.
@Composable
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
)
@Preview(name = "Light mode")
@Preview(
    name = "Dark Mode landscape",
    showBackground = true,
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
    device = "spec:parent=pixel_5,orientation=landscape"
)
@Preview(name = "Light mode landscape", device = "spec:parent=pixel_5,orientation=landscape")
fun TestOnly() {
    AppTheme {
        //AccountUI({})
    }
}
