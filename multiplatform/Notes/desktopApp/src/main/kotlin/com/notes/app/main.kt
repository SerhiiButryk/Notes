package com.notes.app

import androidx.compose.ui.window.Window
import com.notes.ui.theme.AppThemeCommon

fun main() = run {
    applicationTraced {
        Window(
            onCloseRequest = ::exitApplication,
            title = APP_TITLE,
        ) {
            // Set min width & height
            // window.minimumSize = Dimension(1200, 800)
            AppThemeCommon { onThemeChange ->
                EntryScreen(appScope = this@applicationTraced, onThemeChange = onThemeChange)
            }
        }
    }
}