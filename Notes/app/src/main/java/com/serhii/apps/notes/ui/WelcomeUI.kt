/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.auth.types.UIRequestType
import com.serhii.apps.notes.ui.theme.AppMaterialTheme
import com.serhii.apps.notes.ui.state_holders.LoginViewModel

/**
 * Welcome screen
 */

@Composable
fun WelcomeUI(uiState: LoginViewModel.BaseUIState, viewModel: LoginViewModel) {

    val leftPadding = dimensionResource(R.dimen.left_right_padding)
    val rightPadding = dimensionResource(R.dimen.left_right_padding)
    val bottomPadding = dimensionResource(R.dimen.button_bottom_padding)

    Box {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = leftPadding, end = rightPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleUI(title = uiState.title)
            DescriptionUI(desc = uiState.descriptionText)
        }

        val context = LocalContext.current

        ButtonUI(
            text = uiState.buttonText,
            Modifier
                .fillMaxWidth()
                .padding(start = leftPadding, end = rightPadding, bottom = bottomPadding)
                .align(alignment = Alignment.BottomCenter)
        ) {
            viewModel.sendAction(uiState.requestType, context)
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)
@Composable
private fun WelcomeMaterialThemeLightUI() {
    WelcomeMaterialThemeUIForPreview()
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
private fun WelcomeMaterialThemeDarkUI() {
    WelcomeMaterialThemeUIForPreview()
}

@Composable
private fun WelcomeMaterialThemeUIForPreview() {
    val uiState = LoginViewModel.WelcomeUIState(
        "Welcome",
        "Welcome to note app. Manage all your notes here. Firstly you need to create a user. Click \"Continue\" button to proceed.",
        "Continue", UIRequestType.WELCOME_UI
    )

    AppMaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WelcomeUI(uiState, LoginViewModel())
        }
    }
}