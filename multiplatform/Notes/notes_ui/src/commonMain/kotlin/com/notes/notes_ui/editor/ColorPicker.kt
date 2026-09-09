package com.notes.notes_ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.notes.ui.theme.backgroundColor
import dev.mkeeda.arranger.richtext.RgbaColor

private val colorPalette = listOf(
    // Grayscale
    listOf(
        RgbaColor(0xFF000000),
        RgbaColor(0xFF333333),
        RgbaColor(0xFF666666),
        RgbaColor(0xFF999999),
        RgbaColor(0xFFCCCCCC),
        RgbaColor(0xFFE5E5E5),
        RgbaColor(0xFFF2F2F2),
        RgbaColor(0xFFFFFFFF),
    ),

    // Bright colors
    listOf(
        RgbaColor(0xFFFF0000),
        RgbaColor(0xFFFF8000),
        RgbaColor(0xFFFFFF00),
        RgbaColor(0xFF00CC00),
        RgbaColor(0xFF00FFFF),
        RgbaColor(0xFF0080FF),
        RgbaColor(0xFF8000FF),
        RgbaColor(0xFFFF00FF),
    ),

    // Light
    listOf(
        RgbaColor(0xFFFFD6D6),
        RgbaColor(0xFFFFE5D1),
        RgbaColor(0xFFFFF2CC),
        RgbaColor(0xFFE2F0CB),
        RgbaColor(0xFFD9EAF7),
        RgbaColor(0xFFD9D2E9),
        RgbaColor(0xFFEADCF8),
        RgbaColor(0xFFF4CCCC),
    ),

    listOf(
        RgbaColor(0xFFF4CCCC),
        RgbaColor(0xFFFCE5CD),
        RgbaColor(0xFFFFF2CC),
        RgbaColor(0xFFD9EAD3),
        RgbaColor(0xFFD0E0E3),
        RgbaColor(0xFFCFE2F3),
        RgbaColor(0xFFD9D2E9),
        RgbaColor(0xFFEAD1DC),
    ),

    // Medium
    listOf(
        RgbaColor(0xFFEA9999),
        RgbaColor(0xFFF9CB9C),
        RgbaColor(0xFFFFE599),
        RgbaColor(0xFFB6D7A8),
        RgbaColor(0xFFA2C4C9),
        RgbaColor(0xFF9FC5E8),
        RgbaColor(0xFFB4A7D6),
        RgbaColor(0xFFD5A6BD),
    ),

    listOf(
        RgbaColor(0xFFE06666),
        RgbaColor(0xFFF6B26B),
        RgbaColor(0xFFFFD966),
        RgbaColor(0xFF93C47D),
        RgbaColor(0xFF76A5AF),
        RgbaColor(0xFF6FA8DC),
        RgbaColor(0xFF8E7CC3),
        RgbaColor(0xFFC27BA0),
    ),

    // Dark
    listOf(
        RgbaColor(0xFFCC0000),
        RgbaColor(0xFFE69138),
        RgbaColor(0xFFF1C232),
        RgbaColor(0xFF6AA84F),
        RgbaColor(0xFF45818E),
        RgbaColor(0xFF3D85C6),
        RgbaColor(0xFF674EA7),
        RgbaColor(0xFFA64D79),
    ),

    listOf(
        RgbaColor(0xFF990000),
        RgbaColor(0xFFB45F06),
        RgbaColor(0xFFBF9000),
        RgbaColor(0xFF38761D),
        RgbaColor(0xFF134F5C),
        RgbaColor(0xFF1155CC),
        RgbaColor(0xFF351C75),
        RgbaColor(0xFF741B47),
    )
)

@Composable
fun ColorPickerDialog(
    backgroundColor: RgbaColor,
    textColor: RgbaColor,
    onBackgroundColorChange: (RgbaColor) -> Unit,
    onTextColorChange: (RgbaColor) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {

        Surface(
            modifier = Modifier.width(325.dp),
            shape = RoundedCornerShape(4.dp),
            color = backgroundColor(),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                ColorPickerColumn(
                    title = "Background color",
                    selectedColor = backgroundColor,
                    onColorSelected = onBackgroundColorChange
                )

                ColorPickerColumn(
                    title = "Text color",
                    selectedColor = textColor,
                    onColorSelected = onTextColorChange
                )
            }
        }
    }
}

@Composable
private fun ColorPickerColumn(
    title: String,
    selectedColor: RgbaColor,
    onColorSelected: (RgbaColor) -> Unit
) {
    Column(
        modifier = Modifier.width(142.dp)
    ) {

        Text(
            text = title,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        colorPalette.forEach { rowColors ->

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            ) {
                rowColors.forEach { color ->
                    ColorCell(
                        color = color,
                        selected = color == selectedColor,
                        onClick = {
                            onColorSelected(color)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorCell(
    color: RgbaColor,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(Color(color.value))
            .then(
                if (selected) {
                    Modifier.border(
                        width = 1.dp,
                        color = Color.Black
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                modifier = Modifier.size(14.dp),
                tint = if (Color(color.value).luminance() > 0.5f)
                    Color.Black
                else
                    Color.White
            )
        }
    }
}

