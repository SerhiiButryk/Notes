package com.notes.notes_ui.screens.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.notes.notes_ui.NotesViewModel
import com.notes.notes_ui.screens.editor.Tool
import com.notes.notes_ui.screens.editor.ToolsPane
import com.notes.ui.Arrow_up

@Composable
fun ToolsPane(
    modifier: Modifier = Modifier,
    state: RichTextState,
    toolsPaneItems: List<ToolsPane>,
    notes: NotesViewModel.Notes,
) {
    Surface(
        shape = CircleShape,
        shadowElevation = 10.dp,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceBright),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            for (tools in toolsPaneItems) {
                // Add one option
                if (tools.list.size == 1) {
                    val option = tools.list.first()
                    item(key = option.key) {
                        ToolButton(
                            imageVector = option.imageVector,
                            id = option.id,
                            onClick = { option.onClick(state, notes) },
                            animated = option.highlight
                        )
                    }
                    // Add a list of options
                } else {
                    item {
                        ToolsMenu(tools, state, notes)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolButton(
    imageVector: ImageVector? = null,
    @DrawableRes id: Int = 0,
    onClick: () -> Unit,
    forceAnimation: MutableState<Boolean>? = null,
    animated: Boolean = true
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
            targetValue = 120f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 1500, easing = LinearEasing))
        )

        IconButton(
            onClick = onClickListener,
            // Animate button background
            modifier = Modifier.drawBehind {
                val start = forceAnimation?.value ?: clicked
                if (start && animated) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(color1, color2),
                            start = Offset(0f, 0f),
                            end = Offset(offset, offset),
                        )
                    )
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

@Composable
private fun ToolsMenu(tools: ToolsPane, state: RichTextState, notes: NotesViewModel.Notes) {

    var expanded by remember { mutableStateOf(false) }

    Box {

        var startAnimation = remember { mutableStateOf(false) }

        ToolButton(
            onClick = {
                expanded = !expanded
                startAnimation.value = true
            },
            imageVector = Arrow_up,
            forceAnimation = startAnimation
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                startAnimation.value = false
            }
        ) {
            for (tool in tools.list) {
                MenuItem(
                    tool = tool,
                    onAction = { tool.onClick(state, notes) }
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    tool: Tool,
    onAction: () -> Unit
) {
    DropdownMenuItem(
        leadingIcon = {
            if (tool.imageVector != null) {
                Icon(
                    imageVector = tool.imageVector,
                    contentDescription = ""
                )
            } else {
                Icon(
                    painter = painterResource(id = tool.id),
                    contentDescription = ""
                )
            }
        },
        text = { Text(text = tool.text) },
        onClick = onAction
    )
}