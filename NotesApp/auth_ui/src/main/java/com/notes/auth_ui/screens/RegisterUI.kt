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

private const val TAG = "RegisterUI"

@Composable
internal fun RegisterUI(
    state: AuthViewModel.RegisterUIState,
    onRegister: (AuthViewModel.RegisterUIState) -> Unit
) {
    AppTheme {
        RegisterImpl(state, onRegister)
    }
}

@Composable
private fun RegisterImpl(
    state: AuthViewModel.RegisterUIState,
    onRegister: (AuthViewModel.RegisterUIState) -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        val confirmPassword = rememberSaveable { mutableStateOf("") }

        AuthUIAdaptive(
            title = "Register",
            subTitle = "Enter your user email and create a password " +
                    "to access this application",
            emailState = email,
            passwordState = password,
            confirmPasswordState = confirmPassword,
            hasFocus = state.hasFocus,
            innerPadding = innerPadding,
            onEnter = { passwordValue, confirmPasswordValue, emailValue ->
                onRegister(
                    AuthViewModel.RegisterUIState(
                        email = emailValue,
                        password = passwordValue,
                        confirmPassword = confirmPasswordValue
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
private fun RegisterUIPreviewLight() {
    AppTheme {
        RegisterImpl(AuthViewModel.RegisterUIState(), {})
    }
}