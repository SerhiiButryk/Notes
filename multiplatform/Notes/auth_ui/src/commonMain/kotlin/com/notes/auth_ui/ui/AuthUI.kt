package com.notes.auth_ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp

@Composable
fun AuthLayoutWideScreen(
    modifier: Modifier,
    title: String,
    subTitle: String,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    confirmPasswordState: MutableState<String>? = null,
    onLogin: (() -> Unit)? = null,
    onEnter: (String, String, String) -> Unit,
    hasProgress: Boolean,
    emailFieldFocusRequester: FocusRequester,
    passwordFieldFocusRequester: FocusRequester,
) {

    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.verticalScroll(scrollState),
    ) {
        SurfaceContainer(
            modifier =
                Modifier
                    .widthIn(max = 600.dp)
                    .wrapContentHeight(),
        ) {
            Column {
                val modifier =
                    Modifier
                        .padding(20.dp)

                AuthHeader(
                    modifier = modifier,
                    alignment = Alignment.CenterHorizontally,
                    title = title,
                    subTitle = subTitle,
                    onLogin = onLogin
                )

                AuthBody(
                    modifier = modifier,
                    emailState = emailState,
                    confirmPasswordState = confirmPasswordState,
                    emailFieldFocusRequester = emailFieldFocusRequester,
                    passwordFieldFocusRequester = passwordFieldFocusRequester,
                    passwordState = passwordState,
                    onEnter = onEnter,
                    hasProgress = hasProgress,
                )
            }
        }
    }
}

@Composable
fun AuthLayoutLandscapeSmallScreen(
    containerModifier: Modifier,
    mainContentModifier: Modifier,
    title: String,
    subTitle: String,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    confirmPasswordState: MutableState<String>? = null,
    onLogin: (() -> Unit)? = null,
    onEnter: (String, String, String) -> Unit,
    hasProgress: Boolean,
    emailFieldFocusRequester: FocusRequester,
    passwordFieldFocusRequester: FocusRequester,
) {
    Row(modifier = containerModifier) {
        val modifier =
            Modifier
                .fillMaxSize()
                .weight(1f)

        Box(
            modifier = modifier,
        ) {
            SurfaceContainer {
                AuthHeader(
                    modifier = mainContentModifier,
                    bottomPadding = 10.dp,
                    title = title,
                    subTitle = subTitle,
                    onLogin = onLogin
                )
            }
        }

        Box(
            modifier = modifier,
        ) {
            SurfaceContainer {

                val scrollState = rememberScrollState()

                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    AuthBody(
                        modifier = mainContentModifier,
                        emailState = emailState,
                        emailFieldFocusRequester = emailFieldFocusRequester,
                        passwordFieldFocusRequester = passwordFieldFocusRequester,
                        passwordState = passwordState,
                        confirmPasswordState = confirmPasswordState,
                        hasProgress = hasProgress,
                        onEnter = onEnter,
                    )
                }
            }
        }
    }
}

@Composable
fun AuthLayoutCommonScreen(
    containerModifier: Modifier,
    mainContentModifier: Modifier,
    title: String,
    subTitle: String,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    confirmPasswordState: MutableState<String>? = null,
    onLogin: (() -> Unit)? = null,
    onEnter: (String, String, String) -> Unit,
    hasProgress: Boolean,
    emailFieldFocusRequester: FocusRequester,
    passwordFieldFocusRequester: FocusRequester,
) {
    Box(
        modifier = containerModifier,
        contentAlignment = Alignment.Center,
    ) {
        SurfaceContainer {

            val scrollState = rememberScrollState()

            Column(modifier = Modifier.verticalScroll(scrollState)) {
                AuthHeader(
                    modifier = mainContentModifier,
                    alignment = Alignment.CenterHorizontally,
                    title = title,
                    subTitle = subTitle,
                    onLogin = onLogin
                )

                AuthBody(
                    modifier = mainContentModifier,
                    emailState = emailState,
                    confirmPasswordState = confirmPasswordState,
                    emailFieldFocusRequester = emailFieldFocusRequester,
                    passwordFieldFocusRequester = passwordFieldFocusRequester,
                    passwordState = passwordState,
                    hasProgress = hasProgress,
                    onEnter = onEnter,
                )
            }
        }
    }
}