package com.notes.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.lerp
import kotlin.math.max

/**
 * Animated background gradient
 */
@Composable
fun AnimatedBackground(content: @Composable () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val isDarkTheme = isSystemInDarkTheme()
    val easing = CubicBezierEasing(0.45f, 0f, 0.15f, 1f)

    val transition = rememberInfiniteTransition(label = "animated-background")
    val primaryDrift by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = easing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "primary-drift",
    )
    val secondaryDrift by transition.animateFloat(
        initialValue = 1f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 17000, easing = easing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "secondary-drift",
    )
    val glowPulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = easing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow-pulse",
    )
    val primaryColorPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = easing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "primary-color-phase",
    )
    val secondaryColorPhase by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = easing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "secondary-color-phase",
    )
    val tertiaryColorPhase by transition.animateFloat(
        initialValue = 0.68f,
        targetValue = 1.68f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = easing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "tertiary-color-phase",
    )

    val primaryAlpha = if (isDarkTheme) 0.42f else 0.22f
    val secondaryAlpha = if (isDarkTheme) 0.34f else 0.18f
    val tertiaryAlpha = if (isDarkTheme) 0.30f else 0.16f
    val surfaceAlpha = if (isDarkTheme) 0.20f else 0.48f
    val primaryGlow = animatedGradientColor(
        progress = primaryColorPhase,
        colors = listOf(
            colorScheme.primary,
            colorScheme.tertiary,
            colorScheme.secondary,
            colorScheme.primaryContainer,
        ),
    )
    val secondaryGlow = animatedGradientColor(
        progress = secondaryColorPhase,
        colors = listOf(
            colorScheme.secondary,
            colorScheme.primaryContainer,
            colorScheme.tertiaryContainer,
            colorScheme.secondaryContainer,
        ),
    )
    val tertiaryGlow = animatedGradientColor(
        progress = tertiaryColorPhase,
        colors = listOf(
            colorScheme.tertiary,
            colorScheme.secondaryContainer,
            colorScheme.primary,
            colorScheme.tertiaryContainer,
        ),
    )
    val ambientTone = animatedGradientColor(
        progress = tertiaryColorPhase,
        colors = listOf(
            colorScheme.surfaceVariant,
            colorScheme.primaryContainer,
            colorScheme.tertiaryContainer,
            colorScheme.secondaryContainer,
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val radius = max(size.width, size.height) * (0.78f + glowPulse * 0.12f)
                val primaryCenter = Offset(
                    x = size.width * (0.18f + primaryDrift * 0.10f),
                    y = size.height * (0.18f + secondaryDrift * 0.08f),
                )
                val secondaryCenter = Offset(
                    x = size.width * (0.78f + secondaryDrift * 0.08f),
                    y = size.height * (0.72f + primaryDrift * 0.10f),
                )
                val tertiaryCenter = Offset(
                    x = size.width * (0.52f + primaryDrift * 0.06f),
                    y = size.height * (0.45f + secondaryDrift * 0.06f),
                )

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.background,
                            ambientTone.copy(alpha = surfaceAlpha),
                            colorScheme.surface,
                        ),
                    ),
                )
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryGlow.copy(alpha = primaryAlpha * 0.45f),
                            Color.Transparent,
                            tertiaryGlow.copy(alpha = tertiaryAlpha * 0.55f),
                        ),
                        start = Offset(size.width * (0.08f + primaryDrift * 0.06f), -size.height * 0.10f),
                        end = Offset(size.width * (0.94f + secondaryDrift * 0.05f), size.height * 1.08f),
                    ),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryGlow.copy(alpha = primaryAlpha),
                            primaryGlow.copy(alpha = 0f),
                        ),
                        center = primaryCenter,
                        radius = radius,
                    ),
                    radius = radius,
                    center = primaryCenter,
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            secondaryGlow.copy(alpha = secondaryAlpha),
                            secondaryGlow.copy(alpha = 0f),
                        ),
                        center = secondaryCenter,
                        radius = radius * 0.92f,
                    ),
                    radius = radius * 0.92f,
                    center = secondaryCenter,
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            tertiaryGlow.copy(alpha = tertiaryAlpha),
                            tertiaryGlow.copy(alpha = 0f),
                        ),
                        center = tertiaryCenter,
                        radius = radius * 0.72f,
                    ),
                    radius = radius * 0.72f,
                    center = tertiaryCenter,
                )
            }
    ) {
        content()
    }
}

private fun animatedGradientColor(
    progress: Float,
    colors: List<Color>,
): Color {
    val normalizedProgress = progress % 1f
    val scaledProgress = normalizedProgress * colors.size
    val currentIndex = scaledProgress.toInt().coerceAtMost(colors.lastIndex)
    val nextIndex = (currentIndex + 1) % colors.size
    val fraction = scaledProgress - currentIndex

    return lerp(colors[currentIndex], colors[nextIndex], fraction)
}
