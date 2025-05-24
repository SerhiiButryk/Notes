package com.notes.auth_ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import android.util.Log
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

private const val TAG = "LoginUI"

@Composable
fun LoginUI() {
    AppTheme {
        LoginUIImpl()
    }
}

@Composable
fun LoginUIImpl() {

    val viewModel = viewModel<AuthViewModel>()
    val state = viewModel.loginState.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }

        val emailFieldFocusRequester = remember { FocusRequester() }

        LaunchedEffect(false) {
            if (state.value.emailHasFocus) {
                Log.i(TAG, "LoginUIImpl: request focus for email field")
                emailFieldFocusRequester.requestFocus()
            }
        }

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Email input field
                InputTextField(
                    text = email,
                    label = "Enter email",
                    focusRequester = emailFieldFocusRequester,
                    onValueChange = { email = it },
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password input field
                InputTextField(
                    text = password,
                    label = "Enter password",
                    onValueChange = { password = it },
                    keyboardType = KeyboardType.Password
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login button
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Login")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginUIPreviewLight() {
    AppTheme {
        LoginUIImpl()
    }
}

@Preview(
    showBackground = true,
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES
)
@Composable
fun LoginUIPreviewDark() {
    AppTheme {
        LoginUIImpl()
    }
}