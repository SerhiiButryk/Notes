package com.notes.auth_ui.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.notes.ui.isPhoneLandScape
import com.notes.ui.isTabletOrFoldableExpanded

@Composable
fun AuthUIAdaptive(
    title: String,
    subTitle: String,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    confirmPasswordState: MutableState<String>? = null,
    onEnter: (String, String, String) -> Unit,
    innerPadding: PaddingValues,
    hasFocus: Boolean,
    hasProgress: Boolean,
    onLogin: (() -> Unit)? = null
) {
    val emailFieldFocusRequester = remember { FocusRequester() }
    val passFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            if (emailState.value.isEmpty()) {
                emailFieldFocusRequester.requestFocus()
            } else {
                passFieldFocusRequester.requestFocus()
            }
        }
    }

    val sc = currentWindowAdaptiveInfo().windowSizeClass

    val left = innerPadding.calculateLeftPadding(LocalLayoutDirection.current)
    val right = innerPadding.calculateLeftPadding(LocalLayoutDirection.current)

    val containerModifier =
        Modifier
            .padding(start = left, end = right)
            .fillMaxSize()

    val mainContentModifier =
        Modifier
            .padding(20.dp)
            .fillMaxWidth()

    // Add padding specifically for UI in portrait phone and tablet mode
    // to make UI a bit better
    val keyboardPadding =
        Modifier
            .imePadding()

    when {

        isTabletOrFoldableExpanded(sc) -> {
            val modifier = containerModifier
                .then(keyboardPadding)
            AuthLayoutWideScreen(
                modifier = modifier,
                title = title,
                subTitle = subTitle,
                emailState = emailState,
                passwordState = passwordState,
                confirmPasswordState = confirmPasswordState,
                emailFieldFocusRequester = emailFieldFocusRequester,
                passwordFieldFocusRequester = passFieldFocusRequester,
                hasProgress = hasProgress,
                onLogin = onLogin,
                onEnter = onEnter,
            )
        }

        isPhoneLandScape(sc) -> {
            AuthLayoutLandscapeSmallScreen(
                containerModifier = containerModifier,
                mainContentModifier = mainContentModifier,
                title = title,
                subTitle = subTitle,
                emailState = emailState,
                passwordState = passwordState,
                confirmPasswordState = confirmPasswordState,
                emailFieldFocusRequester = emailFieldFocusRequester,
                passwordFieldFocusRequester = passFieldFocusRequester,
                hasProgress = hasProgress,
                onLogin = onLogin,
                onEnter = onEnter,
            )
        }

        // Other configurations
        else -> {
            val modifier = containerModifier.then(keyboardPadding)
            AuthLayoutCommonScreen(
                containerModifier = modifier,
                mainContentModifier = mainContentModifier,
                title = title,
                subTitle = subTitle,
                emailState = emailState,
                passwordState = passwordState,
                confirmPasswordState = confirmPasswordState,
                emailFieldFocusRequester = emailFieldFocusRequester,
                passwordFieldFocusRequester = passFieldFocusRequester,
                hasProgress = hasProgress,
                onLogin = onLogin,
                onEnter = onEnter,
            )
        }

    }
}
