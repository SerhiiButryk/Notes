/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.state_holders.LoginViewModel

@Composable
fun ForgotPasswordUI(uiState: LoginViewModel.ForgotPasswordUIState, viewModel: LoginViewModel) {

    val leftPadding = dimensionResource(R.dimen.left_right_padding)
    val rightPadding = dimensionResource(R.dimen.left_right_padding)

    val columModifiers = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(start = leftPadding, end = rightPadding)
        .verticalScroll(rememberScrollState())

    val doneAction = {
        // TODO: Implement
    }

    Column(
        columModifiers,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val inputFocusPassword = remember { FocusRequester() }
        val focusModifierPassword = Modifier.focusRequester(inputFocusPassword)

        TitleUI(title = uiState.title)

        PasswordFieldUI(label = uiState.passwordFiledLabel, hint = uiState.passwordFiledHint,
            actionKeyboard = ImeAction.Next,
            initValue = { uiState.password }, modifier = focusModifierPassword
        ) { newText ->
            uiState.password = newText
        }

        PasswordFieldUI(label = uiState.confirmPasswordFiledLabel, hint = uiState.confirmPasswordFiledHint,
            doneAction = doneAction,
            actionKeyboard = ImeAction.Done,
            initValue = { uiState.confirmPassword }
        ) { newText ->
            uiState.confirmPassword = newText
        }

        ButtonUI(text = uiState.buttonText, modifier = Modifier.fillMaxWidth()) {
            doneAction()
        }

        viewModel.requestKeyboard(inputFocusPassword)
    }
}

@Composable
fun ForgotPasswordUIPreview() {
//    ForgotPasswordUI()
}