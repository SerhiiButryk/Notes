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
import com.notes.ui.AnimatedBackground
import com.notes.ui.NetworkStateMessage

@Composable
fun LoginUIImpl(
    state: LoginUIState,
    onLogin: (LoginUIState) -> Unit,
    content: @Composable (
        title: String,
        subTitle: String,
        emailState: MutableState<String>,
        passwordState: MutableState<String>,
        onEnter: (String, String, String) -> Unit,
        innerPadding: PaddingValues
    ) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            NetworkStateMessage()

            AnimatedBackground {

                val email = rememberSaveable { mutableStateOf(state.email) }
                val password = rememberSaveable { mutableStateOf("") }

                content(
                    "Welcome again !",
                    "Sign in using your email and password",
                    email,
                    password,
                    { passwordValue, _, emailValue ->
                        onLogin(
                            LoginUIState(
                                email = emailValue,
                                password = passwordValue,
                            ),
                        )
                    },
                    innerPadding,
                )

            }
        }

    }
}