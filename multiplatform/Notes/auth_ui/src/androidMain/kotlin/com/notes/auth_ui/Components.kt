package com.notes.auth_ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.ui.AccentButton
import com.notes.ui.InputTextField
import com.notes.ui.isPhoneLandScape
import com.notes.ui.isTabletOrFoldableExpanded
import com.notes.ui.theme.SurfaceColor

@Composable
internal fun AuthUIAdaptive(
    title: String,
    subTitle: String,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    confirmPasswordState: MutableState<String>? = null,
    onEnter: (String, String, String) -> Unit,
    innerPadding: PaddingValues,
    hasFocus: Boolean,
    progress: Boolean,
    onLogin: (() -> Unit)? = null
) {
    val emailFieldFocusRequester = remember { FocusRequester() }
    val passFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            if (emailState.value.isEmpty()) {
                emailFieldFocusRequester.requestFocus()
            } else {
                passFieldFocusRequester.requestFocus()
            }
        }
    }

    val sc = currentWindowAdaptiveInfo().windowSizeClass

    val left = innerPadding.calculateLeftPadding(LocalLayoutDirection.current)
    val right = innerPadding.calculateLeftPadding(LocalLayoutDirection.current)

    val containerModifier =
        Modifier
            .padding(start = left, end = right)
            .fillMaxSize()

    val mainContentModifier =
        Modifier
            .padding(20.dp)
            .fillMaxWidth()

    // Add padding specifically for UI in portrait phone and tablet mode
    // to make UI a bit better
    val keyboardPadding =
        Modifier
            .imePadding()

    when {
        isTabletOrFoldableExpanded(sc) -> {

            val scrollState = rememberScrollState()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = containerModifier
                    .then(keyboardPadding)
                    .verticalScroll(scrollState),
            ) {
                SurfaceContainer(
                    modifier =
                        Modifier
                            .widthIn(max = 600.dp)
                            .wrapContentHeight(),
                ) {
                    Column {
                        val modifier =
                            Modifier
                                .padding(20.dp)

                        AuthHeader(
                            modifier = modifier,
                            alignment = Alignment.CenterHorizontally,
                            title = title,
                            subTitle = subTitle,
                            onLogin = onLogin
                        )

                        AuthBody(
                            modifier = modifier,
                            emailState = emailState,
                            emailFieldFocusRequester = emailFieldFocusRequester,
                            passwordFieldFocusRequester = passFieldFocusRequester,
                            passwordState = passwordState,
                            confirmPasswordState = confirmPasswordState,
                            onEnter = onEnter,
                            progress = progress,
                        )
                    }
                }
            }
        }

        isPhoneLandScape(sc) -> {
            Row(modifier = containerModifier) {
                val modifier =
                    Modifier
                        .fillMaxSize()
                        .weight(1f)

                Box(
                    modifier = modifier,
                ) {
                    SurfaceContainer {
                        AuthHeader(
                            modifier = mainContentModifier,
                            bottomPadding = 10.dp,
                            title = title,
                            subTitle = subTitle,
                            onLogin = onLogin
                        )
                    }
                }

                Box(
                    modifier = modifier,
                ) {
                    SurfaceContainer {

                        val scrollState = rememberScrollState()

                        Column(modifier = Modifier.verticalScroll(scrollState)) {
                            AuthBody(
                                modifier = mainContentModifier,
                                emailState = emailState,
                                emailFieldFocusRequester = emailFieldFocusRequester,
                                passwordFieldFocusRequester = passFieldFocusRequester,
                                passwordState = passwordState,
                                confirmPasswordState = confirmPasswordState,
                                progress = progress,
                                onEnter = onEnter,
                            )
                        }
                    }
                }
            }
        }

        // Other configurations
        else -> {
            Box(
                modifier = containerModifier.then(keyboardPadding),
                contentAlignment = Alignment.Center,
            ) {
                SurfaceContainer {

                    val scrollState = rememberScrollState()

                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        AuthHeader(
                            modifier = mainContentModifier,
                            alignment = Alignment.CenterHorizontally,
                            title = title,
                            subTitle = subTitle,
                            onLogin = onLogin
                        )

                        AuthBody(
                            modifier = mainContentModifier,
                            emailState = emailState,
                            emailFieldFocusRequester = emailFieldFocusRequester,
                            passwordFieldFocusRequester = passFieldFocusRequester,
                            passwordState = passwordState,
                            confirmPasswordState = confirmPasswordState,
                            progress = progress,
                            onEnter = onEnter,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun AuthHeader(
    modifier: Modifier,
    alignment: Alignment.Horizontal = Alignment.Start,
    bottomPadding: Dp = 0.dp,
    title: String,
    subTitle: String,
    onLogin: (() -> Unit)?
) {
    Column(
        modifier = modifier,
    ) {
        val textAlign = if (alignment == Alignment.Start) TextAlign.Start else TextAlign.Center

        Header(
            text = title,
            modifier =
                Modifier
                    .padding(all = 10.dp)
                    .fillMaxWidth(),
            textAlign = textAlign,
        )

        SubHeader(
            text = subTitle,
            modifier =
                Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = bottomPadding)
                    .fillMaxWidth(),
            textAlign = textAlign,
        )

        if (onLogin != null) {
            Text(
                text = "Have you already got your account ? Go ahead and sign in !",
                fontSize = 18.sp,
                style = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                modifier = Modifier.padding(top = 10.dp).clickable { onLogin() },
                color = MaterialTheme.colorScheme.primary
            )
        }

    }
}

@Composable
internal fun AuthBody(
    modifier: Modifier,
    emailState: MutableState<String>,
    emailFieldFocusRequester: FocusRequester,
    passwordFieldFocusRequester: FocusRequester,
    passwordState: MutableState<String>,
    confirmPasswordState: MutableState<String>? = null,
    onEnter: (String, String, String) -> Unit,
    progress: Boolean,
) {
    var password by passwordState
    var email by emailState
    val confirmPassword: MutableState<String>? = confirmPasswordState

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Email input field
        InputTextField(
            text = email,
            label = "Enter email",
            focusRequester = emailFieldFocusRequester,
            onValueChange = { email = it },
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Next,
        )

        val focusManager = LocalFocusManager.current

        val doneAction = {
            onEnter(password, confirmPassword?.value ?: "", email)
            focusManager.clearFocus()
        }

        val keyboardActions =
            KeyboardActions(
                onDone = { doneAction() },
            )

        // Password input field
        InputTextField(
            text = password,
            label = "Enter password",
            focusRequester = passwordFieldFocusRequester,
            onValueChange = { password = it },
            keyboardType = KeyboardType.Password,
            modifier = Modifier.fillMaxWidth(),
            imeAction = if (confirmPassword == null) ImeAction.Done else ImeAction.Next,
            keyboardActions = if (confirmPassword == null) keyboardActions else KeyboardActions.Default,
        )

        if (confirmPassword != null) {
            // Confirm password input field
            InputTextField(
                text = confirmPassword.value,
                label = "Confirm password",
                onValueChange = { confirmPassword.value = it },
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Done,
                keyboardActions =
                    KeyboardActions(
                        onDone = { doneAction() },
                    ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (progress) {
            CircularProgressIndicator()
        } else {
            // Login button
            AccentButton(
                onClick = { onEnter(password, confirmPassword?.value ?: "", email) },
                label = "Continue"
            )
        }
    }
}

@Composable
fun SurfaceContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 8.dp,
        modifier =
            Modifier
                .padding(all = 10.dp)
                .then(modifier),
        color = SurfaceColor(),
    ) {
        content()
    }
}

@Composable
fun Header(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        fontSize = 24.sp,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier,
        textAlign = textAlign,
    )
}

@Composable
fun SubHeader(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        fontSize = 18.sp,
        style = TextStyle(fontWeight = FontWeight.Bold, textAlign = textAlign),
        modifier = modifier,
    )
}
