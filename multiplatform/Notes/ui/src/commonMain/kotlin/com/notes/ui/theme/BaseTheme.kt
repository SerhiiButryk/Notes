package com.notes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme =
    darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80,
        surface = Color(0xFF282A2D),
    )

val LightColorScheme =
    lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40,
        surface = Color(0xFFBDC1C6),
//        surface = Color(0xFFFFFBFE),
//        onPrimary = Color.White,
//        onSecondary = Color.White,
//        onTertiary = Color.White,
//        onBackground = Color(0xFF1C1B1F),
//        onSurface = Color(0xFF1C1B1F),
    )

@Composable
fun BaseAppTheme(
    colorScheme: ColorScheme,
    typography: androidx.compose.material3.Typography,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}

@Composable
fun AppThemeCommon(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    BaseAppTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
