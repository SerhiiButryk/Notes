package com.notes.app.notes

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Dimension

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "notes",
        ) {

            // Set min width & height
            window.minimumSize = Dimension(800, 600)

            EntryScreen()
        }
    }
