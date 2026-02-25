package com.notes.auth_ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.notes.ui.NetworkStateMessage

@Composable
fun RegisterUIImpl(
    onRegister: (RegisterUIState) -> Unit,
    content: @Composable (
        title: String,
        subTitle: String,
        emailState: MutableState<String>,
        passwordState: MutableState<String>,
        confirmPasswordState: MutableState<String>,
        onEnter: (String, String, String) -> Unit,
        innerPadding: PaddingValues
    ) -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        val confirmPassword = rememberSaveable { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            NetworkStateMessage()

            content(
                "Register",
                "Enter your user email and create a password " +
                        "to access this application",
                email,
                password,
                confirmPassword,
                { passwordValue, confirmPasswordValue, emailValue ->
                    onRegister(
                        RegisterUIState(
                            email = emailValue,
                            password = passwordValue,
                            confirmPassword = confirmPasswordValue,
                        ),
                    )
                },
                innerPadding,
            )

        }
    }
}