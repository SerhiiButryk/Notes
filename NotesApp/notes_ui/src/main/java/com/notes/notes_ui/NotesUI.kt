package com.notes.notes_ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.notes.notes_ui.richeditor.NotesEditorUI
import com.notes.ui.SearchBarField
import com.notes.ui.isMiddleWidth
import com.notes.ui.isTabletOrFoldableExpanded
import com.notes.ui.theme.AppTheme

@Composable
fun NotesUI(
    modifier: Modifier = Modifier,
    addButtonAction: () -> Unit = {},
    onSettingsAction: () -> Unit = {}
) {
    AppTheme {
        NotesUIImpl(addButtonAction = addButtonAction, onSettingsAction = onSettingsAction)
    }
}

@Composable
private fun NotesUIImpl(addButtonAction: () -> Unit = {}, onSettingsAction: () -> Unit = {}) {
    Row {
        // TODO: Might need to pass this from outside. Recalculation every time might be slow.
        val sizeClass = currentWindowAdaptiveInfo().windowSizeClass

        // Show list + editor on large screens
        if (isTabletOrFoldableExpanded(sizeClass)) {

            val showSettingsIcon = false // Not showing for large screens

            NotesNavRail()

            // Show list + editor if we have large width
            if (isMiddleWidth(sizeClass)) {

                val modifierList = Modifier.weight(0.4f)
                val modifierEditor = Modifier.weight(1.0f)

                NotesListLayout(
                    showList = true,
                    showSettingsIcon = showSettingsIcon,
                    modifier = modifierList,
                    addButtonAction = addButtonAction,
                    onSettingsAction = onSettingsAction
                )

                NotesEditorUI(modifier = modifierEditor)

                // Show only list if width is not large enough
            } else {
                NotesListLayout(
                    showSettingsIcon = showSettingsIcon,
                    addButtonAction = addButtonAction,
                    onSettingsAction = onSettingsAction
                )
            }

            // Show only list on small screens like phones
        } else {
            NotesListLayout(addButtonAction = addButtonAction, onSettingsAction = onSettingsAction)
        }
    }
}

@Composable
private fun NotesListLayout(
    modifier: Modifier = Modifier,
    showList: Boolean = false,
    showSettingsIcon: Boolean = true,
    addButtonAction: () -> Unit,
    onSettingsAction: () -> Unit = {}
) {
    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        SearchBarField(
            trailingIcon = {
                if (showSettingsIcon) {
                    IconButton(
                        onClick = { onSettingsAction() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { addButtonAction() },
        ) {
            Icon(Icons.Filled.Add, null)
        }
    }) { innerPadding ->
        NotesList(modifier = Modifier.padding(innerPadding), showList = showList)
    }
}

@Preview
@Preview(
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
    device = "spec:parent=pixel_5,orientation=landscape"
)
@Composable
private fun NotesUIPrev() {
    NotesUIImpl()
}
