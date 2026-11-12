import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import api.data.Notes
import com.notes.notes_ui.editor.ColorPickerDialog
import com.notes.notes_ui.editor.getBackgroundTextColor
import com.notes.notes_ui.editor.getTextColor
import com.notes.notes_ui.models.Tool
import com.notes.notes_ui.models.Tools
import com.notes.ui.AlertDialogUI
import com.notes.ui.ToggleButton
import dev.mkeeda.arranger.richtext.editor.RichTextState

@Composable
fun DesktopToolsBar(
    state: RichTextState,
    tools: Tools,
    notes: Notes,
) {

    var showDialogForTool by rememberSaveable { mutableStateOf<Tool?>(null) }

    var showColorPicker by remember { mutableStateOf(false) }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp),
            ),
        horizontalArrangement = Arrangement.Center,
    ) {
        for (tools in tools.collection) {
            for (tool in tools.list) {
                ToggleButton(
                    imageVector = tool.imageVector,
                    painter = tool.getIcon(),
                    isActive = tool.isActive(state),
                    isEnabled = tool.isEnable(state),
                    onClick = {
                        if (tool.showColorPickerDialog) {
                            showColorPicker = true
                            showDialogForTool = tool
                        } else if (tool.showConfirmDialog) {
                            showDialogForTool = tool
                        } else {
                            tool.onClick(state, notes)
                        }
                    },
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
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