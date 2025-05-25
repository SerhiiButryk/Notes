package com.notes.auth_ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notes.ui.InputTextField
import com.notes.ui.theme.AppTheme

private const val TAG = "RegisterUI"

@Composable
internal fun RegisterUI() {
    AppTheme {
        RegisterImpl()
    }
}

@Composable
private fun RegisterImpl() {

    val viewModel = viewModel<AuthViewModel>()
    val state = viewModel.registerState.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var confirmPassword by rememberSaveable { mutableStateOf("") }

        val emailFieldFocusRequester = remember { FocusRequester() }

        LaunchedEffect(false) {
            if (state.value.emailHasFocus) {
                emailFieldFocusRequester.requestFocus()
            }
        }

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SurfaceContainer {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Register",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 24.dp, top = 10.dp)
                    )

                    // Email input field
                    InputTextField(
                        text = email,
                        label = "Enter email",
                        focusRequester = emailFieldFocusRequester,
                        onValueChange = { email = it },
                        keyboardType = KeyboardType.Email
                    )

                    // Password input field
                    InputTextField(
                        text = password,
                        label = "Enter password",
                        onValueChange = { password = it },
                        keyboardType = KeyboardType.Password
                    )

                    // Confirm password input field
                    InputTextField(
                        text = confirmPassword,
                        label = "Confirm password",
                        onValueChange = { confirmPassword = it },
                        keyboardType = KeyboardType.Password
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Register button
                    Button(
                        onClick = {
                            viewModel.register(
                                AuthViewModel.RegisterUIState(
                                    email = email,
                                    password = password,
                                    confirmPassword = confirmPassword
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Text(text = "Register")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginUIPreviewLight() {
    AppTheme {
        RegisterImpl()
    }
}

@Preview(
    showBackground = true,
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES
)
@Composable
private fun LoginUIPreviewDark() {
    AppTheme {
        RegisterImpl()
    }
}