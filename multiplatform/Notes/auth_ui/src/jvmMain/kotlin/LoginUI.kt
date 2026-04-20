import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.notes.auth_ui.ui.AuthLayoutWideScreen
import com.notes.auth_ui.ui.LoginUIImpl
import com.notes.auth_ui.data.LoginUIState
import kotlin.String

@Composable
fun LoginScreenImpl(
    state: LoginUIState,
    onLogin: (LoginUIState) -> Unit
) {

    LoginUIImpl(
        state = state,
        onLogin = onLogin
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