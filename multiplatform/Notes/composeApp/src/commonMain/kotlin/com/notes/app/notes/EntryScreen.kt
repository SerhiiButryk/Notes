package com.notes.app.notes

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
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import api.data.Notes
import com.notes.auth_ui.ui.AuthLayoutWideScreen
import com.notes.auth_ui.ui.LoginUIImpl
import com.notes.auth_ui.ui.LoginUIState
import com.notes.auth_ui.ui.OnBoardingUIImpl
import com.notes.auth_ui.ui.RegisterUIImpl
import com.notes.auth_ui.ui.RegisterUIState
import com.notes.notes_ui.AccountUI
import com.notes.notes_ui.Interactor
import com.notes.notes_ui.NotesCommonUI
import com.notes.notes_ui.RepoCallback
import com.notes.notes_ui.Repository
import com.notes.notes_ui.SettingsUI
import com.notes.notes_ui.data.AccountInfo
import com.notes.notes_ui.data.getToolsList
import com.notes.ui.AccountInfoScreen
import com.notes.ui.LoginScreen
import com.notes.ui.OnBoardingNoteScreen
import com.notes.ui.PreviewScreen
import com.notes.ui.RegistrationScreen
import com.notes.ui.SettingsScreen
import com.notes.ui.createNavBackStack
import com.notes.ui.destinations
import kotlinx.coroutines.flow.Flow

@Composable
@Preview
fun EntryScreen() {

    val backstack = createNavBackStack(default = OnBoardingNoteScreen, elements = destinations)

    NavDisplay(
        backStack = backstack,
        entryProvider = entryProvider {

            entry(LoginScreen) {
                LoginScreenImpl(
                    onLogin = {
                        backstack.clear()
                        backstack.add(PreviewScreen)
                    }
                )
            }

            entry(RegistrationScreen) {
                RegisterScreenImpl(
                    onRegister = {
                        backstack.clear()
                        backstack.add(LoginScreen)
                    }
                )
            }

            entry(PreviewScreen) {
                NotesScreenImpl(onSettingsClick = {
                    backstack.add(SettingsScreen)
                })
            }

            entry(AccountInfoScreen) {
                AccountScreenImpl()
            }

            entry(SettingsScreen) {
                SettingsScreenImpl(onAccountClick = {
                    backstack.add(AccountInfoScreen)
                })
            }

            entry(OnBoardingNoteScreen) {
                OnBoardingScreenImpl(onContinue = {
                    backstack.add(RegistrationScreen)
                })
            }

        }
    )
}

@Composable
fun NotesScreenImpl(onSettingsClick: () -> Unit) {

    val repo = object : Repository {
        override fun getNotes(): Flow<List<Notes>> {
            TODO("Not yet implemented")
        }

        override fun getNotes(id: Long): Flow<Notes?> {
            TODO("Not yet implemented")
        }

        override fun saveNote(note: Notes, onNewAdded: suspend (Long) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun deleteNote(note: Notes, callback: (Long) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun clear() {
            TODO("Not yet implemented")
        }
    }

    val inter = Interactor(repo, object : RepoCallback {
        override fun onNoteAdded(id: Long) {
        }

        override fun onNoteDeleted(id: Long) {
        }

        override fun onNoteNavigateBack() {
        }
    })

    NotesCommonUI(
        notes = listOf(Notes("content1", id = 1), Notes("content2", id = 2)),
        note = Notes(),
        toolsPaneItems = getToolsList(inter),
        onAddAction = {},
        onSelectAction = {},
        onTextChanged = { _ ->

        },
        onSettingsClick = { onSettingsClick() },
        onBackClick = {},
        showNavRail = true,
        isPhoneSize = false
    )

}

@Composable
fun AccountScreenImpl() {
    AccountUI({}, {}, {}, AccountInfo())
}

@Composable
fun SettingsScreenImpl(onAccountClick: () -> Unit) {
    SettingsUI({}, { onAccountClick() }, {})
}

@Composable
fun RegisterScreenImpl(onRegister: (RegisterUIState) -> Unit) {

    val state = RegisterUIState()

    val onLogin = {}

    RegisterUIImpl(
        onRegister = { onRegister(it) },
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
fun OnBoardingScreenImpl(onContinue: () -> Unit) {
    OnBoardingUIImpl(
        onContinue = onContinue,
        modifier = Modifier.widthIn(max = 800.dp),
    )
}

@Composable
fun LoginScreenImpl(onLogin: (LoginUIState) -> Unit) {

    val state = LoginUIState()

    LoginUIImpl(
        state = state,
        onLogin = {
            onLogin(it)
        }
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