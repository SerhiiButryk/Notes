package com.notes.auth_ui

import com.notes.ui.Screen

private var simulationMode = false

/**
 * Defining destinations for debug app.
 * We can easily skip auth screens for testing.
 * Note: this is intended for app module usage only.
 */
fun getDestination(): Screen {
    return if (simulationMode) {
        // Open Note Preview screen
        com.notes.notes_ui.getStartDestination()
    } else {
        // Open Auth screen
        getStartRoute()
    }
}