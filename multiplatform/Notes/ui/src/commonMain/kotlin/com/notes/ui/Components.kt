package com.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.PlatformAPIs
import api.PlatformAPIs.logger
import kotlinx.coroutines.delay

/**
 * Material input text component
 */

@Composable
fun InputTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    focusRequester: FocusRequester? = null,
    onValueChange: (String) -> Unit = {},
    label: String = "",
    keyboardType: KeyboardType,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val localModifier = if (focusRequester == null) modifier else modifier.focusRequester(focusRequester)

    var passwordShown by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        leadingIcon = {
            // Show an icon base on keyboard type
            if (keyboardType == KeyboardType.Password) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "",
                )
            } else if (keyboardType == KeyboardType.Email) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "",
                )
            }
        },
        trailingIcon = {
            if (keyboardType == KeyboardType.Password) {
                PasswordEye(
                    passwordShown = passwordShown,
                    onTogglePasswordVisibility = { passwordShown = !passwordShown },
                )
            }
        },
        value = text,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            showKeyboardOnFocus = true,
            autoCorrectEnabled = false,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
        modifier = localModifier,
        maxLines = 1,
        visualTransformation = if (passwordShown || keyboardType == KeyboardType.Email) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        supportingText = {
// TODO Enable error message for input validation errors
            //            ErrorMessage()
        },
    )
}

@Composable
internal fun PasswordEye(
    passwordShown: Boolean,
    onTogglePasswordVisibility: () -> Unit,
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
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Error !!!",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

/**
 * Material dialog component
 */

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
                },
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text("Cancel")
            }
        },
    )
}

/**
 * Search bar component
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarField(
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    onBackClick: () -> Unit = {}
) {

    val focusManager = LocalFocusManager.current

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                expanded = false,
                query = "",
                onQueryChange = { },
                onSearch = { }, // Collapse on search submission
                onExpandedChange = { },
                placeholder = { Text("Search your notes...") },
                leadingIcon = {
//                    if (true) {
                    IconButton(onClick = {
                        onBackClick()
//                            active = false
//                            query = "" // Clear query when collapsing with back button
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
//                    } else {
//                        Icon(Icons.Default.Search, contentDescription = "Search icon")
//                    }
                },
                trailingIcon = {
//                    if (/*active && query.isNotEmpty()*/false) {
//                        IconButton(onClick = { /*query = ""*/ }) {
//                            Icon(Icons.Default.Close, contentDescription = "Clear search")
//                        }
//                    }

                    trailingIcon?.invoke()
                },
            )
        },
        expanded = false,
        onExpandedChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
            .onFocusEvent {
                if (it.hasFocus) {
                    focusManager.clearFocus()
                }
            },
    ) {}
}

/**
 * Material button component
 */

@Composable
fun AccentButton(
    onClick: () -> Unit, label: String, modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick() },
        modifier = modifier.fillMaxWidth().background(
            brush = Brush.horizontalGradient(listOf(Color(0xFF9DCEFF), Color(0xFFC58BF2))),
            shape = RoundedCornerShape(50.dp)
        ),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Text(text = label, color = Color.White)
    }
}

/**
 * Simple top app bar with title
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(
    title: String, onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

/**
 * Shows a message with a network state automatically
 */
@Composable
fun NetworkStateMessage() {

    var networkIsAvailable by rememberSaveable { mutableStateOf(true) }
    var show by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(false) {
        if (!PlatformAPIs.netStateManager.isNetworkAvailable()) {
            // Network is NOT available show it permanently
            networkIsAvailable = false
            show = true
        }
        PlatformAPIs.netStateManager.observerChanges().collect { netState ->
            logger.logi("NetworkStateMessage() netState = $netState, currState = $networkIsAvailable")
            if (netState.networkIsAvailable && !networkIsAvailable) {
                // Network is now available show it
                networkIsAvailable = true
                show = true
                // Hide after some timeout
                delay(3000)
                show = false
            } else if (!netState.networkIsAvailable) {
                // Network is NOT available show it permanently
                networkIsAvailable = false
                show = true
            }
        }
    }

    val color = if (networkIsAvailable) Color(0xFF46923c) else Color(0xFFc30010)

    if (show) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(color),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
        ) {
            val message = if (networkIsAvailable) "Network is available" else "Network is not available"
            Text(text = message, fontSize = 16.sp)
        }
    }

}
