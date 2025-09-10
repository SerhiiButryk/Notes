package com.notes.auth_ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.notes.auth_ui.AuthUIAdaptive
import com.notes.auth_ui.AuthViewModel
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

        if (email.value != state.email)
            email.value = state.email

        AuthUIAdaptive(
            title = "Welcome again !",
            subTitle = "Login using your email and password",
            emailState = email,
            passwordState = password,
            hasFocus = state.hasFocus,
            innerPadding = innerPadding,
            onEnter = { passwordValue, _, emailValue ->
                onLogin(
                    AuthViewModel.LoginUIState(
                        email = emailValue,
                        password = passwordValue
                    )
                )
            }
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