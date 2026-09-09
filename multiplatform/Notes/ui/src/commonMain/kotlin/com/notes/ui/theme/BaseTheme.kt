package com.notes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import api.data.AppSettings
import kotlinx.coroutines.launch

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
        surface = Color(0xFFFFFBFE),
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
    typography: androidx.compose.material3.Typography = Typography,
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
    content: @Composable (onThemeChange: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()

    // Setting app theme
    val isDark = isSystemInDarkTheme()
    var theme by remember {
        val colorScheme =
            when {
                isDark -> DarkColorScheme
                else -> LightColorScheme
            }
        mutableStateOf(colorScheme)
    }

    LaunchedEffect(false) {
        val isDark = AppSettings.isDarkTheme()
        if (isDark) {
            theme = DarkColorScheme
        } else {
            theme = LightColorScheme
        }
    }

    val onThemeChange = {
        val colorScheme =
            if (theme == LightColorScheme) {
                scope.launch {
                    AppSettings.setTheme(isDark = true)
                }
                DarkColorScheme
            } else {
                scope.launch {
                    AppSettings.setTheme(isDark = false)
                }
                LightColorScheme
            }
        theme = colorScheme
    }

    BaseAppTheme(
        colorScheme = theme,
    ) {
        content(onThemeChange)
    }
}
