package com.notes.auth_ui.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
internal fun RegisterUI(
    state: RegisterUIState,
    onRegister: (RegisterUIState) -> Unit,
    onLogin: (() -> Unit)
) {

    RegisterUIImpl(
        onRegister = onRegister,
    ) {

        title: String,
        subTitle: String,
        emailState: MutableState<String>,
        passwordState: MutableState<String>,
        confirmPasswordState: MutableState<String>,
        onEnter: (String, String, String) -> Unit,
        innerPadding: PaddingValues ->

        AuthUIAdaptive(
            title = title,
            subTitle = subTitle,
            emailState = emailState,
            passwordState = passwordState,
            confirmPasswordState = confirmPasswordState,
            hasFocus = state.hasFocus,
            innerPadding = innerPadding,
            hasProgress = false,
            onEnter = onEnter,
            onLogin = onLogin
        )

    }
}
