package com.notes.auth_ui.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.notes.auth_ui.data.LoginUIState

@Composable
fun LoginUI(
    state: LoginUIState,
    onLogin: (LoginUIState) -> Unit,
    title: String,
    subTitle: String,
) {

    LoginUIImpl(
        state = state,
        onLogin = onLogin,
    ) {
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