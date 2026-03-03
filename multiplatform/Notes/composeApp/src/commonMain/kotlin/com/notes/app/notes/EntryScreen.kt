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
import api.data.Notes
import com.notes.auth_ui.ui.*
import com.notes.notes_ui.Interactor
import com.notes.notes_ui.NotesCommonUI
import com.notes.notes_ui.RepoCallback
import com.notes.notes_ui.Repository
import com.notes.notes_ui.data.getToolsList
import com.notes.ui.theme.AppThemeCommon
import kotlinx.coroutines.flow.Flow

@Composable
@Preview
fun EntryScreen() {
    AppThemeCommon {
//        LoginScreenImpl()
//        OnBoardingScreenImpl()
//         RegisterScreenImpl()
        NotesScreenImpl()
    }
}

@Composable
fun NotesScreenImpl() {

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
        onSettingsClick = {},
        onBackClick = {},
        showNavRail = true,
        isPhoneSize = false
    )

}

@Composable
fun AccountScreenImpl() {

}

@Composable
fun SettingsScreenImpl() {

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