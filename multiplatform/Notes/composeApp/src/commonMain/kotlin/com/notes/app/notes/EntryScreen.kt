package com.notes.app.notes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.auth_ui.ui.AuthLayoutWideScreen
import com.notes.auth_ui.ui.LoginUIImpl
import com.notes.auth_ui.ui.LoginUIState
import com.notes.auth_ui.ui.OnBoardingUIImpl
import com.notes.auth_ui.ui.RegisterUIImpl
import com.notes.auth_ui.ui.RegisterUIState
import com.notes.ui.theme.AppThemeCommon

@Composable
@Preview
fun EntryScreen() {
    AppThemeCommon {
//        LoginScreenImpl()
//        OnBoardingScreenImpl()
         RegisterScreenImpl()
    }
}

@Composable
fun RegisterScreenImpl() {

    val state = RegisterUIState()

    val onLogin = {}

    RegisterUIImpl(
        onRegister = {},
    ) {

        title: String,
        subTitle: String,
        emailState: MutableState<String>,
        passwordState: MutableState<String>,
        confirmPasswordState: MutableState<String>,
        onEnter: (String, String, String) -> Unit,
        _ ->

        val emailFieldFocusRequester = remember { FocusRequester() }
        val passFieldFocusRequester = remember { FocusRequester() }

        LaunchedEffect(state.hasFocus) {
            if (state.hasFocus) {
                if (emailState.value.isEmpty()) {
                    emailFieldFocusRequester.requestFocus()
                } else {
                    passFieldFocusRequester.requestFocus()
                }
            }
        }

        AuthLayoutWideScreen(
            modifier = Modifier.fillMaxSize(),
            title = title,
            subTitle = subTitle,
            emailState = emailState,
            passwordState = passwordState,
            confirmPasswordState = confirmPasswordState,
            emailFieldFocusRequester = emailFieldFocusRequester,
            passwordFieldFocusRequester = passFieldFocusRequester,
            hasProgress = false,
            onLogin = onLogin,
            onEnter = onEnter,
        )

    }
}

@Composable
fun OnBoardingScreenImpl() {
    OnBoardingUIImpl(
        onContinue = {},
        modifier = Modifier.widthIn(max = 800.dp),
    )
}

@Composable
fun LoginScreenImpl() {

    val state = LoginUIState()

    LoginUIImpl(
        state = state,
        onLogin = {}
    ) {

        title: String,
        subTitle: String,
        emailState: MutableState<String>,
        passwordState: MutableState<String>,
        onEnter: (String, String, String) -> Unit,
        _ ->

        val emailFieldFocusRequester = remember { FocusRequester() }
        val passFieldFocusRequester = remember { FocusRequester() }

        LaunchedEffect(state.hasFocus) {
            if (state.hasFocus) {
                if (emailState.value.isEmpty()) {
                    emailFieldFocusRequester.requestFocus()
                } else {
                    passFieldFocusRequester.requestFocus()
                }
            }
        }

        AuthLayoutWideScreen(
            modifier = Modifier.fillMaxSize(),
            title = title,
            subTitle = subTitle,
            emailState = emailState,
            passwordState = passwordState,
            emailFieldFocusRequester = emailFieldFocusRequester,
            passwordFieldFocusRequester = passFieldFocusRequester,
            hasProgress = state.showProgress,
            onEnter = onEnter,
        )

    }

}