package com.notes.auth_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import com.notes.ui.InputTextField
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
    emailHasFocus: Boolean
) {

    val emailFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(false) {
        if (emailHasFocus) {
            emailFieldFocusRequester.requestFocus()
        }
    }

    val sc = currentWindowAdaptiveInfo().windowSizeClass

    val containerModifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()

    val mainContentModifier = Modifier
        .padding(20.dp)
        .fillMaxWidth()

    when {

        // Tablet
        (sc.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
                && sc.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)) -> {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = containerModifier
            ) {

                SurfaceContainer(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .wrapContentHeight()
                ) {

                    Column {

                        val modifier = Modifier
                            .padding(20.dp)

                        AuthHeader(
                            modifier = modifier,
                            alignment = Alignment.CenterHorizontally,
                            title = title,
                            subTitle = subTitle
                        )

                        AuthBody(
                            modifier = modifier,
                            emailState = emailState,
                            emailFieldFocusRequester = emailFieldFocusRequester,
                            passwordState = passwordState,
                            confirmPasswordState = confirmPasswordState,
                            onEnter = onEnter
                        )
                    }

                }

            }

        }

        // Phone landscape
        (sc.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
                && sc.isHeightAtLeastBreakpoint(0)) -> {

            Row(modifier = containerModifier) {

                val modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)

                Box(
                    modifier = modifier
                ) {
                    SurfaceContainer {
                        AuthHeader(
                            modifier = mainContentModifier,
                            bottomPadding = 10.dp,
                            title = title,
                            subTitle = subTitle
                        )
                    }
                }

                Box(
                    modifier = modifier
                ) {
                    SurfaceContainer {
                        AuthBody(
                            modifier = mainContentModifier,
                            emailState = emailState,
                            emailFieldFocusRequester = emailFieldFocusRequester,
                            passwordState = passwordState,
                            confirmPasswordState = confirmPasswordState,
                            onEnter = onEnter
                        )
                    }
                }

            }

        }

        // Phone portrait
        else -> {

            Box(
                modifier = containerModifier,
                contentAlignment = Alignment.Center
            ) {

                SurfaceContainer {

                    Column {

                        AuthHeader(
                            modifier = mainContentModifier,
                            alignment = Alignment.CenterHorizontally,
                            title = title,
                            subTitle = subTitle
                        )

                        AuthBody(
                            modifier = mainContentModifier,
                            emailState = emailState,
                            emailFieldFocusRequester = emailFieldFocusRequester,
                            passwordState = passwordState,
                            confirmPasswordState = confirmPasswordState,
                            onEnter = onEnter
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
    subTitle: String
) {
    Column(
        modifier = modifier
    ) {

        val textAlign = if (alignment == Alignment.Start) TextAlign.Start else TextAlign.Center

        Header(
            text = title, modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth(), textAlign = textAlign
        )

        SubHeader(
            text = subTitle,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = bottomPadding)
                .fillMaxWidth(),
            textAlign = textAlign
        )
    }
}

@Composable
internal fun AuthBody(
    modifier: Modifier,
    emailState: MutableState<String>,
    emailFieldFocusRequester: FocusRequester,
    passwordState: MutableState<String>,
    confirmPasswordState: MutableState<String>? = null,
    onEnter: (String, String, String) -> Unit,
) {

    var password by passwordState
    var email by emailState
    val confirmPassword: MutableState<String>? = confirmPasswordState

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {

        // Email input field
        InputTextField(
            text = email,
            label = "Enter email",
            focusRequester = emailFieldFocusRequester,
            onValueChange = { email = it },
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        // Password input field
        InputTextField(
            text = password,
            label = "Enter password",
            onValueChange = { password = it },
            keyboardType = KeyboardType.Password,
            modifier = Modifier.fillMaxWidth()
        )

        if (confirmPassword != null) {
            // Confirm password input field
            InputTextField(
                text = confirmPassword.value,
                label = "Confirm password",
                onValueChange = { confirmPassword.value = it },
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        Button(
            onClick = {
                onEnter(password, confirmPassword?.value ?: "", email)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Text(text = "Continue")
        }
    }
}

@Composable
fun SurfaceContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 8.dp,
        modifier = Modifier
            .padding(all = 10.dp)
            .then(modifier),
        color = SurfaceColor()
    ) {
        content()
    }
}

@Composable
fun Header(text: String, modifier: Modifier = Modifier, textAlign: TextAlign? = null) {
    Text(
        text = text,
        fontSize = 24.sp,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun SubHeader(text: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        fontSize = 18.sp,
        style = TextStyle(fontWeight = FontWeight.Bold, textAlign = textAlign),
        modifier = modifier
    )
}