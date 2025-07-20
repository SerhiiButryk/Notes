package com.notes.auth_ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.notes.ui.InputTextField
import com.notes.ui.theme.AppTheme

private const val TAG = "LoginUI"

@Composable
internal fun LoginUI(
    state: AuthViewModel.LoginUIState,
    onLogin: (AuthViewModel.LoginUIState) -> Unit
) {
    AppTheme {
        LoginUIImpl(state, onLogin)
    }
}

@Composable
private fun LoginUIImpl(
    state: AuthViewModel.LoginUIState,
    onLogin: (AuthViewModel.LoginUIState) -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }

        val emailFieldFocusRequester = remember { FocusRequester() }

        LaunchedEffect(false) {
            if (state.emailHasFocus) {
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
                            .heightIn(max = 400.dp)
                    ) {

                        Column {

                            val modifier = Modifier
                                .padding(20.dp)

                            LoginHeader(
                                modifier = modifier,
                                alignment = Alignment.CenterHorizontally
                            )

                            LoginBody(modifier, email, emailFieldFocusRequester, password, onLogin)
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
                            LoginHeader(modifier = mainContentModifier, bottomPadding = 10.dp)
                        }
                    }

                    Box(
                        modifier = modifier
                    ) {
                        SurfaceContainer {
                            LoginBody(
                                mainContentModifier,
                                email,
                                emailFieldFocusRequester,
                                password,
                                onLogin
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

                            LoginHeader(
                                modifier = mainContentModifier,
                                alignment = Alignment.CenterHorizontally
                            )

                            LoginBody(mainContentModifier, email, emailFieldFocusRequester, password, onLogin)
                        }

                    }

                }

            }
        }

    }
}

@Composable
private fun LoginBody(
    modifier: Modifier,
    emailState: MutableState<String>,
    emailFieldFocusRequester: FocusRequester,
    passwordState: MutableState<String>,
    onLogin: (AuthViewModel.LoginUIState) -> Unit
) {

    var password by passwordState
    var email by emailState

    Column(
        modifier = modifier,
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

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field
        InputTextField(
            text = password,
            label = "Enter password",
            onValueChange = { password = it },
            keyboardType = KeyboardType.Password,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        Button(
            onClick = {
                onLogin(
                    AuthViewModel.LoginUIState(
                        email = email,
                        password = password
                    )
                )
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
private fun LoginHeader(
    modifier: Modifier,
    alignment: Alignment.Horizontal = Alignment.Start,
    bottomPadding: Dp = 0.dp
) {
    Column(
        modifier = modifier
    ) {

        val textAlign = if (alignment == Alignment.Start) TextAlign.Start else TextAlign.Center

        Header(text = "Welcome again !", modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth(), textAlign = textAlign)

        SubHeader(
            text = "Login using your email and password",
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = bottomPadding)
                .fillMaxWidth(),
            textAlign = textAlign
        )
    }
}

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES
)
@Composable
private fun LoginUIPreviewLight() {
    AppTheme {
        LoginUIImpl(AuthViewModel.LoginUIState(), {})
    }
}