package com.notes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import api.data.AppSettings

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val SurfaceDarkModeColor = Color(0xFF1e1e1e)

@Composable
fun surfaceColor(): Color {
    val darkMode = AppSettings.isDarkThemeEnabled
    return if (darkMode) SurfaceDarkModeColor else MaterialTheme.colorScheme.surface
}

@Composable
fun backgroundColor(): Color {
    val darkMode = AppSettings.isDarkThemeEnabled
    return if (darkMode) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainerHigh
}

@Composable
fun iconColor(isEnabled: Boolean): Color {
    val darkMode = AppSettings.isDarkThemeEnabled
    return if (darkMode) {
        if (isEnabled) Color.White else Color(0xFF616161)
    } else {
        if (isEnabled) Color.Black else Color(0xFFBDBDBD)
    }
}
