package com.notes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun InputTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    focusRequester: FocusRequester? = null,
    onValueChange: (String) -> Unit = {},
    label: String = "",
    keyboardType: KeyboardType
) {
    val localModifier =
        if (focusRequester == null) modifier else modifier.focusRequester(focusRequester)

    var passwordShown by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        leadingIcon = {
            // Show an icon base on keyboard type
            if (keyboardType == KeyboardType.Password)
                Icon(imageVector = KEY_ICON, contentDescription = "")
            else if (keyboardType == KeyboardType.Email)
                Icon(imageVector = EMAIL_ICON, contentDescription = "")
        },
        trailingIcon = {
            if (keyboardType == KeyboardType.Password) PasswordEye(
                passwordShown = passwordShown,
                onTogglePasswordVisibility = { passwordShown = !passwordShown })
        },
        value = text,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            showKeyboardOnFocus = true,
            autoCorrectEnabled = false
        ),
        modifier = localModifier,
        maxLines = 1,
        visualTransformation = if (passwordShown || keyboardType == KeyboardType.Email) VisualTransformation.None else PasswordVisualTransformation(),
        supportingText = {
// TODO Enable error message for input validation errors
            //            ErrorMessage()
        }
    )
}

@Composable
internal fun PasswordEye(
    passwordShown: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    val image = if (passwordShown) EYE_HIDDEN_ICON else EYE_OPEN_ICON

    IconButton(onClick = onTogglePasswordVisibility) {
        Icon(imageVector = image, contentDescription = "")
    }
}

@Composable
internal fun ErrorMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error !!!",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AlertDialogUI(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector = Icons.Default.Info,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}