package com.notes.app.notes

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.notes.os.JVMInitProvider
import com.notes.ui.theme.AppThemeCommon

const val APP_TITLE = "Notes"

fun main() {

    // Set application title
    System.setProperty("apple.awt.application.name", APP_TITLE)

    JVMInitProvider.create()

    application {

        Window(
            onCloseRequest = ::exitApplication,
            title = APP_TITLE,
        ) {

            // Example menu bar
            MenuBar {
                Menu("File", mnemonic = 'F') {
                    Item(
                        "New",
                        onClick = { println("New clicked") },
                        shortcut = KeyShortcut(Key.N, ctrl = true)
                    )
                    Item("Open", onClick = { /* ... */ })
                    Separator()
                    Item("Exit", onClick = { exitApplication() })
                }

                Menu("Edit", mnemonic = 'E') {
                    CheckboxItem("Show Grid", checked = true, onCheckedChange = { /* ... */ })
                }

                Menu("Help") {
                    Item("About", onClick = { /* ... */ })
                }
            }

            // Set min width & height
            //window.minimumSize = Dimension(1200, 800)

            AppThemeCommon {
                EntryScreen()
            }
        }

    }
}