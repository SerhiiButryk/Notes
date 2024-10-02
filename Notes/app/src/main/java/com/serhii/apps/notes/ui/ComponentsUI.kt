/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serhii.apps.notes.R

@Composable
fun ButtonUI(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TitleUI(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)
    )
}

@Composable
fun DescriptionUI(desc: String) {
    Text(
        text = desc,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUI(
    hint: String,
    hasBackButton: Boolean = false,
    menuOptionsList: List<MenuOptions> = emptyList(),
    backAction: (() -> Unit)? = null
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ) {

        var query by remember { mutableStateOf("") }

        DockedSearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.fillMaxWidth(),
                    query = query,
                    onQueryChange = {
                        query = it
                    },
                    onSearch = { },
                    expanded = false,
                    enabled = true,
                    placeholder = { Text(text = hint) },
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // Back button
                            if (hasBackButton) {
                                IconButton(onClick = { backAction?.invoke() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                    )
                                }
                            }

                            // Search button
                            IconButton(onClick = { /* Do smth */ }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "",
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                            }
                        }
                    },
                    trailingIcon = {
                        Menu(menuOptionsList)
                    },
                    colors = SearchBarDefaults.colors().inputFieldColors,
                    interactionSource = null,
                    onExpandedChange = {}
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = SearchBarDefaults.dockedShape,
            colors = SearchBarDefaults.colors(),
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            content = {},
        )
    }
}

class MenuOptions(
    val textId: Int,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val addDivider: Boolean = false
)

@Composable
fun Menu(menuOptionsList: List<MenuOptions>) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize()
    ) {

        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (option in menuOptionsList) {

                DropdownMenuItem(
                    text = { Text(text = stringResource(id = option.textId), style = MaterialTheme.typography.bodySmall) },
                    onClick = {
                        option.onClick()
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(option.icon, contentDescription = null)
                    }
                )

                if (option.addDivider) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Stable
open class DialogUIState(
    val title: Int = -1,
    val message: Int = -1,
    val dialogDismissible: Boolean = true,
    val hasCancelButton: Boolean = false,
    val onConfirm: () -> Unit = {},
    val onCancel: () -> Unit = {},
    val positiveBtn: Int = android.R.string.ok,
    val negativeBtn: Int = android.R.string.cancel,
    var isOpen: Boolean = false
)

@Composable
fun BasicDialogUI(dialogState: DialogUIState) {

    var openDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            if (dialogState.dialogDismissible) {
                openDialog = false
                dialogState.isOpen = false
                dialogState.onCancel()
            }
        },
        title = {
            Text(
                text = stringResource(id = dialogState.title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = stringResource(id = dialogState.message),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Text(
                text = stringResource(id = dialogState.positiveBtn),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(15.dp)
                    .clickable {
                        openDialog = false
                        dialogState.isOpen = false
                        dialogState.onConfirm()
                    }
            )
        },
        dismissButton = {
            if (dialogState.hasCancelButton) {
                Text(
                    text = stringResource(id = dialogState.negativeBtn),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(15.dp)
                        .clickable {
                            openDialog = false
                            dialogState.isOpen = false
                            dialogState.onCancel()
                        }
                )
            }
        }
    )
}

@Composable
fun PasswordFieldUI(
    label: String, modifier: Modifier? = null,
    doneAction: (() -> Unit)? = null, actionKeyboard: ImeAction = ImeAction.Next,
    hint: String, getValue: () -> String, onValueChanged: (String) -> Unit
) {

    var showPassword by rememberSaveable { mutableStateOf(false) }
    var inputValue by rememberSaveable { mutableStateOf("") }

    inputValue = getValue()

    val bottomPadding = dimensionResource(R.dimen.bottom_input_field_padding)

    val defaultModifiers = Modifier
        .padding(bottom = bottomPadding)
        .fillMaxWidth()

    val textStyle = MaterialTheme.typography.bodyMedium
    val labelStyle = MaterialTheme.typography.bodySmall

    OutlinedTextField(
        modifier = if (modifier != null) defaultModifiers.then(modifier) else defaultModifiers,
        value = inputValue,
        onValueChange = { newText ->
            inputValue = newText
            onValueChanged(newText)
        },
        label = {
            Text(text = label, style = labelStyle)
        },
        textStyle = textStyle,
        singleLine = true,
        maxLines = 1,
        placeholder = {
            Text(text = hint, style = labelStyle)
        },
        shape = RoundedCornerShape(percent = 20),
        // Setup password filed transformation
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        // Setup additional keyboard mode
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrect = false,
            imeAction = actionKeyboard
        ),
        // Setup password eye icon
        trailingIcon = {
            val onClick = { showPassword = !showPassword }
            if (showPassword) {
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = ""
                    )
                }
            } else {
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = ""
                    )
                }
            }
        },
        // Setup additional keyboard actions
        keyboardActions = KeyboardActions(onDone = { doneAction?.invoke() })
    )
}

@Composable
fun EmailFieldUI(
    label: String,
    hint: String,
    getValue: () -> String,
    modifier: Modifier,
    onValueChanged: (String) -> Unit
) {

    var inputValue by rememberSaveable { mutableStateOf("") }

    inputValue = getValue()

    val bottomPadding = dimensionResource(R.dimen.bottom_input_field_padding)

    val defaultModifiers = Modifier
        .padding(bottom = bottomPadding)
        .fillMaxWidth()

    val textStyle = MaterialTheme.typography.bodyMedium
    val labelStyle = MaterialTheme.typography.bodySmall

    OutlinedTextField(
        modifier = defaultModifiers.then(modifier),
        value = inputValue,
        onValueChange = { newText ->
            inputValue = newText
            onValueChanged(newText)
        },
        label = {
            Text(text = label, style = labelStyle)
        },
        textStyle = textStyle,
        singleLine = true,
        maxLines = 1,
        placeholder = {
            Text(text = hint, style = labelStyle)
        },
        shape = RoundedCornerShape(percent = 20),
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
    )
}