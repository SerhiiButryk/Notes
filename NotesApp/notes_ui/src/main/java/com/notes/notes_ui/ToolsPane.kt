package com.notes.notes_ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.mohamedrejeb.richeditor.model.RichTextState
import com.notes.notes_ui.NotesViewModel.ToolsPane

@Composable
fun ToolsPane(modifier: Modifier = Modifier, state: RichTextState, toolsPaneItems: List<ToolsPane>) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        for (item in toolsPaneItems) {
            item(key = item.key) {
                ToolButton(
                    imageVector = item.imageVector,
                    id = item.id
                ) { item.onClick(state) }
            }
        }
    }
}

@Composable
private fun ToolButton(
    imageVector: ImageVector? = null,
    @DrawableRes id: Int = 0,
    onClick: () -> Unit
) {

    Surface(shape = CircleShape) {

        var clicked by rememberSaveable { mutableStateOf(false) }

        val onClickListener = {
            onClick()
            clicked = !clicked
        }

        val color1 = MaterialTheme.colorScheme.surfaceBright
        val color2 = MaterialTheme.colorScheme.primaryContainer

        val infiniteTransition = rememberInfiniteTransition()
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 70f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 1500, easing = LinearEasing))
        )

        IconButton(onClick = onClickListener,
            // Animate button background
            modifier = Modifier.drawBehind {
                if (clicked) {
                    drawRect(brush = Brush.linearGradient(
                        colors = listOf(color1, color2),
                        start = Offset(0f, 0f),
                        end = Offset(offset, offset),
                    ))
                }
            }) {
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = ""
                )
            } else {
                Icon(
                    painter = painterResource(id = id),
                    contentDescription = ""
                )
            }
        }

    }

}