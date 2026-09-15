package com.notes.notes_ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import api.data.Notes
import com.notes.notes_ui.editor.ColorPickerDialog
import com.notes.notes_ui.editor.getBackgroundTextColor
import com.notes.notes_ui.editor.getTextColor
import com.notes.notes_ui.models.Tool
import com.notes.notes_ui.models.ToolCollection
import com.notes.notes_ui.models.Tools
import com.notes.ui.AlertDialogUI
import com.notes.ui.Arrow_up
import dev.mkeeda.arranger.richtext.editor.RichTextState

@Composable
fun ToolsBar(
    state: RichTextState,
    tools: Tools,
    notes: Notes,
) {
    var showDialogForTool by rememberSaveable { mutableStateOf<Tool?>(null) }

    var showColorPicker by remember { mutableStateOf(false) }

    LazyRow(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        for (tools in tools.collection) {
            // Add one option
            if (tools.list.size == 1) {
                val option = tools.list.first()
                item(key = option.key) {
                    ToolButton(
                        imageVector = option.imageVector,
                        icon = option.getIcon(),
                        onClick = {
                            if (option.showColorPickerDialog) {
                                showColorPicker = true
                                showDialogForTool = option
                            } else if (option.showConfirmDialog) {
                                showDialogForTool = option
                            } else {
                                option.onClick(state, notes)
                            }
                        },
                        animated = option.highlight,
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

    if (showColorPicker) {
        val dismiss = {
            showColorPicker = false
            showDialogForTool = null
        }
        val textColor = getTextColor(state)
        val backgroundColor = getBackgroundTextColor(state)
        ColorPickerDialog(
            backgroundColor = backgroundColor,
            textColor = textColor,
            onBackgroundColorChange = {
                showDialogForTool?.onColorPicked(state, null, it)
                dismiss()
            },
            onTextColorChange = {
                showDialogForTool?.onColorPicked(state, it, null)
                dismiss()
            },
            onDismiss = dismiss
        )
    } else if (showDialogForTool != null) {
        AlertDialogUI(
            onDismissRequest = {
                showDialogForTool = null
            },
            onConfirmation = {
                showDialogForTool!!.onClick(state, notes)
                showDialogForTool = null
            },
            dialogTitle = showDialogForTool!!.title,
            dialogText = showDialogForTool!!.message,
        )
    }
}

@Composable
private fun ToolButton(
    imageVector: ImageVector? = null,
    icon: Painter? = null,
    onClick: () -> Unit,
    forceAnimation: MutableState<Boolean>? = null,
    animated: Boolean = true,
) {
    val backgroundColor =
        if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        }

    Surface(shape = CircleShape, color = backgroundColor) {
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
            animationSpec = infiniteRepeatable(tween(durationMillis = 1500, easing = LinearEasing)),
        )

        IconButton(
            onClick = onClickListener,
            // Animate button background
            modifier =
                Modifier.drawBehind {
                    val start = forceAnimation?.value ?: clicked
                    if (start && animated) {
                        drawRect(
                            brush =
                                Brush.linearGradient(
                                    colors = listOf(color1, color2),
                                    start = Offset(0f, 0f),
                                    end = Offset(offset, offset),
                                ),
                        )
                    }
                },
        ) {
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = "",
                )
            } else {
                Icon(
                    painter = icon!!,
                    contentDescription = "",
                )
            }
        }
    }
}

@Composable
private fun ToolsMenu(
    tools: ToolCollection,
    state: RichTextState,
    notes: Notes,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        val startAnimation = remember { mutableStateOf(false) }

        ToolButton(
            onClick = {
                expanded = !expanded
                startAnimation.value = true
            },
            imageVector = Arrow_up,
            forceAnimation = startAnimation,
        )

        val dismiss = {
            expanded = false
            startAnimation.value = false
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = dismiss,
        ) {
            for (tool in tools.list) {
                MenuItem(
                    tool = tool,
                    onAction = {
                        tool.onClick(state, notes)
                        dismiss()
                    },
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    tool: Tool,
    onAction: () -> Unit,
) {
    DropdownMenuItem(
        leadingIcon = {
            if (tool.imageVector != null) {
                Icon(
                    imageVector = tool.imageVector,
                    contentDescription = "",
                )
            } else {
                Icon(
                    painter = tool.getIcon()!!,
                    contentDescription = "",
                )
            }
        },
        text = { Text(text = tool.text) },
        onClick = onAction,
    )
}
