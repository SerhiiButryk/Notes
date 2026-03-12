package com.notes.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
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
            AppThemeCommon {
                Menu(appScope = this@applicationTraced)
                EntryScreen()
            }
        }
    }
}

@Composable
fun FrameWindowScope.Menu(appScope: ApplicationScope) {
    // Example menu bar
    MenuBar {
        Menu("File", mnemonic = 'F') {
            Item(
                "New",
                onClick = { println("New clicked") },
                shortcut = KeyShortcut(Key.N, ctrl = true),
            )
            Item("Open", onClick = { /* ... */ })
            Separator()
            Item("Exit", onClick = { appScope.exitApplication() })
        }

        Menu("Edit", mnemonic = 'E') {
            CheckboxItem("Show Grid", checked = true, onCheckedChange = { /* ... */ })
        }

        Menu("Help") {
            Item("About", onClick = { /* ... */ })
        }
    }
}
