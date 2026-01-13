package com.notes.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Animated background gradient
 */
@Composable
fun AnimatedBackground(content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "flow")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "offset"
    )

    val color1 = MaterialTheme.colorScheme.outline.value
    val color2 = MaterialTheme.colorScheme.onPrimary.value
    val color3 = MaterialTheme.colorScheme.secondaryContainer.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val brush = Brush.linearGradient(
                    colors = listOf(Color(color1), Color(color2), Color(color3)),
                    start = Offset(offset, offset),
                    end = Offset(offset + 500f, offset + 1000f)
                )
                drawRect(brush)
            }
    ) {
        content()
    }
}
