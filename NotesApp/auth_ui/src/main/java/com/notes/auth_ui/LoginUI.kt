package com.notes.auth_ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

        AuthUIAdaptive(
            title = "Welcome again !",
            subTitle = "Login using your email and password",
            emailState = email,
            passwordState = password,
            emailHasFocus = state.emailHasFocus,
            innerPadding = innerPadding,
            onEnter = { passwordValue, _, emailValue ->
                onLogin(
                    AuthViewModel.LoginUIState(
                        email = passwordValue,
                        password = emailValue
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