package com.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CommonIcon actual constructor(resource: Any) {

    actual val resource: Any = resource

    @Composable
    actual fun painter(): Painter {
        val id = resource as Int
        return painterResource(id = id)
    }
}