/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.auth.types.UIRequestType
import com.serhii.apps.notes.ui.state_holders.LoginViewModel
import com.serhii.apps.notes.ui.state_holders.LoginViewModel.LoginUIState
import com.serhii.apps.notes.ui.theme.AppMaterialTheme

/**
 * User login and registration screen
 */

@Composable
fun AuthorizationUI(uiState: LoginViewModel.BaseUIState, viewModel: LoginViewModel) {

    val leftPadding = dimensionResource(R.dimen.left_right_padding)
    val rightPadding = dimensionResource(R.dimen.left_right_padding)

    val columModifiers = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(start = leftPadding, end = rightPadding)
        .verticalScroll(rememberScrollState())

    Column(
        columModifiers,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val inputFocusEmail = remember { FocusRequester() }
        val focusModifierEmail = Modifier.focusRequester(inputFocusEmail)

        val inputFocusPassword = remember { FocusRequester() }
        val focusModifierPassword = Modifier.focusRequester(inputFocusPassword)

        val context = LocalContext.current

        val isRegistrationUI = uiState is LoginViewModel.RegistrationUIState

        val doneAction = {
            val requestType =
                if (isRegistrationUI) UIRequestType.REGISTRATION else UIRequestType.PASSWORD_LOGIN
            viewModel.proceed(requestType, context, authModel = viewModel.authModel)
        }

        TitleUI(title = uiState.title)

        EmailFieldUI(label = uiState.emailFiledLabel, hint = uiState.emailFiledHint,
            getValue = { viewModel.authModel.email }, modifier = focusModifierEmail
        ) { newText ->
            viewModel.authModel.email = newText
        }

        PasswordFieldUI(label = uiState.passwordFiledLabel, hint = uiState.passwordFiledHint,
            doneAction = if (isRegistrationUI) null else doneAction,
            actionKeyboard = if (isRegistrationUI) ImeAction.Next else ImeAction.Done,
            getValue = { viewModel.authModel.password }, modifier = focusModifierPassword
        ) { newText ->
            viewModel.authModel.password = newText
        }

        if (uiState is LoginViewModel.RegistrationUIState) {
            PasswordFieldUI(label = uiState.confirmPasswordFiledLabel,
                doneAction = doneAction, actionKeyboard = ImeAction.Done,
                hint = uiState.confirmPasswordFiledHint,
                getValue = { viewModel.authModel.confirmPassword }) { newText ->
                viewModel.authModel.confirmPassword = newText
            }
        } else {
            Text(
                text = stringResource(id = R.string.forgot_password),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(bottom = dimensionResource(id = R.dimen.padding_small))
                    .clickable {
                        viewModel.proceed(UIRequestType.FORGOT_PASSWORD_UI, context)
                    },
                color = MaterialTheme.colorScheme.secondary
            )
        }

        val bottomPaddingMax = dimensionResource(R.dimen.button_bottom_padding)
        val bottomPaddingMin = dimensionResource(R.dimen.bottom_input_field_padding)

        val hasBiometrics = uiState is LoginUIState && uiState.hasBiometric

        val loginButtonModifiers = if (hasBiometrics)
            Modifier
                .padding(bottom = bottomPaddingMin)
                .fillMaxWidth()
        else Modifier
            .padding(bottom = bottomPaddingMax)
            .fillMaxWidth()

        ButtonUI(text = uiState.buttonText, modifier = loginButtonModifiers) {
            doneAction()
        }

        if (uiState is LoginUIState && uiState.hasBiometric) {

            // Add biometrics button

            val biometricButtonModifiers = Modifier
                .padding(bottom = bottomPaddingMax)
                .fillMaxWidth()

            ButtonUI(text = uiState.biometricButtonText, modifier = biometricButtonModifiers) {
                viewModel.proceed(UIRequestType.BIOMETRIC_LOGIN, context)
            }
        }

        // Request for email or password field
        if (viewModel.authModel.email.isEmpty()) {
            viewModel.requestKeyboard(inputFocusEmail)
        } else {
            viewModel.requestKeyboard(inputFocusPassword)
        }
    }

    val openDialog = uiState.openDialog
    if (openDialog && !uiState.dialogState.isOpen) {
        BasicDialogUI(dialogState = uiState.dialogState)
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)
@Composable
private fun AuthorizationUILightPreview() {
    AuthorizationUIForPreview()
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
private fun AuthorizationUIDarkPreview() {
    AuthorizationUIForPreview()
}

@Composable
private fun AuthorizationUIForPreview() {

    val uiState = LoginUIState(
        title = "Welcome",
        emailFiledLabel = "Email",
        emailFiledHint = "Type email",
        passwordFiledLabel = "Password",
        passwordFiledHint = "Type password",
        buttonText = "Login",
        hasBiometric = true,
        biometricButtonText = "Biometrics",
        uiRequestType = UIRequestType.UNLOCK
    )

    AppMaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AuthorizationUI(uiState, LoginViewModel())
        }
    }
}