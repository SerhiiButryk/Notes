package com.notes.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

    val primaryDriftState = transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = easing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "primary-drift"
    )
    val secondaryDriftState = transition.animateFloat(
        initialValue = 1f, targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(17000, easing = easing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "secondary-drift"
    )
    val glowPulseState = transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = easing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow-pulse"
    )
    val primaryColorPhaseState = transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = easing),
            repeatMode = RepeatMode.Restart
        ),
        label = "primary-color-phase"
    )
    val secondaryColorPhaseState = transition.animateFloat(
        initialValue = 0.35f, targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = easing),
            repeatMode = RepeatMode.Restart
        ),
        label = "secondary-color-phase"
    )
    val tertiaryColorPhaseState = transition.animateFloat(
        initialValue = 0.68f, targetValue = 1.68f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = easing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tertiary-color-phase"
    )

    val primaryAlpha = if (isDarkTheme) 0.42f else 0.22f
    val secondaryAlpha = if (isDarkTheme) 0.34f else 0.18f
    val tertiaryAlpha = if (isDarkTheme) 0.30f else 0.16f
    val surfaceAlpha = if (isDarkTheme) 0.20f else 0.48f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {

                val primaryDrift = primaryDriftState.value
                val secondaryDrift = secondaryDriftState.value
                val glowPulse = glowPulseState.value
                val primaryColorPhase = primaryColorPhaseState.value
                val secondaryColorPhase = secondaryColorPhaseState.value
                val tertiaryColorPhase = tertiaryColorPhaseState.value

                val primaryGlow = animatedGradientColor(
                    primaryColorPhase,
                    listOf(
                        colorScheme.primary,
                        colorScheme.tertiary,
                        colorScheme.secondary,
                        colorScheme.primaryContainer
                    )
                )

                val secondaryGlow = animatedGradientColor(
                    secondaryColorPhase,
                    listOf(
                        colorScheme.secondary,
                        colorScheme.primaryContainer,
                        colorScheme.tertiaryContainer,
                        colorScheme.secondaryContainer
                    )
                )

                val tertiaryGlow = animatedGradientColor(
                    tertiaryColorPhase,
                    listOf(
                        colorScheme.tertiary,
                        colorScheme.secondaryContainer,
                        colorScheme.primary,
                        colorScheme.tertiaryContainer
                    )
                )

                val ambientTone = animatedGradientColor(
                    tertiaryColorPhase,
                    listOf(
                        colorScheme.surfaceVariant,
                        colorScheme.primaryContainer,
                        colorScheme.tertiaryContainer,
                        colorScheme.secondaryContainer
                    )
                )

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
                        start = Offset(
                            size.width * (0.08f + primaryDrift * 0.06f),
                            -size.height * 0.10f
                        ),
                        end = Offset(
                            size.width * (0.94f + secondaryDrift * 0.05f),
                            size.height * 1.08f
                        ),
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

/**
 * Styled chip with a text
 */
@Composable
fun BoxScope.StyledChip(text: String) {

    SuggestionChip(
        onClick = {},
        label = {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .align(Alignment.TopEnd),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = if (isSystemInDarkTheme())
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.secondaryContainer,
            labelColor = Color.Black
        ),
        border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true,
            borderColor = Color.White,          // Custom border color
            disabledBorderColor = Color.LightGray,  // Border color when disabled
            borderWidth = 1.dp                      // Thicker stroke width
        ),
        /*elevation = SuggestionChipDefaults.suggestionChipElevation(
            elevation = 4.dp,          // Default floating state elevation
            pressedElevation = 8.dp,   // Elevation when the user presses down
            hoveredElevation = 6.dp,   // Elevation when hovered (Desktop/Web)
            focusedElevation = 6.dp    // Elevation when focused via keyboard/accessibility
        )*/
    )

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
