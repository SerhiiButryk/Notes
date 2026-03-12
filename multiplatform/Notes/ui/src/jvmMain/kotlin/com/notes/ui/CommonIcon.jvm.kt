package com.notes.ui

import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CommonIcon actual constructor(resource: Any) {
    actual val resource: Any = resource

    @androidx.compose.runtime.Composable
    actual fun painter(): Painter {
        val res = resource as DrawableResource
        return org.jetbrains.compose.resources.painterResource(resource = res)
    }
}