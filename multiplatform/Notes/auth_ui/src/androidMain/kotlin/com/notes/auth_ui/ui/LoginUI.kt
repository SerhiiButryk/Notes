package com.notes.auth_ui.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun LoginUI(
    state: LoginUIState,
    onLogin: (LoginUIState) -> Unit,
) {

    LoginUIImpl(
        state = state,
        onLogin = onLogin
    ) {

        title: String,
        subTitle: String,
        emailState: MutableState<String>,
        passwordState: MutableState<String>,
        onEnter: (String, String, String) -> Unit,
        innerPadding: PaddingValues ->

        AuthUIAdaptive(
            title = title,
            subTitle = subTitle,
            emailState = emailState,
            passwordState = passwordState,
            hasFocus = state.hasFocus,
            innerPadding = innerPadding,
            hasProgress = state.showProgress,
            onEnter = onEnter,
        )
    }

}