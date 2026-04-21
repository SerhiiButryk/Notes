import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.notes.auth_ui.ui.AuthLayoutWideScreen
import com.notes.auth_ui.ui.RegisterUIImpl
import com.notes.auth_ui.data.RegisterUIState
import kotlin.String

@Composable
fun RegisterScreenImpl(
    state: RegisterUIState,
    onRegister: (RegisterUIState) -> Unit,
    onLogin: () -> Unit,
    title: String,
    subTitle: String,
) {

    RegisterUIImpl(
        onRegister = { onRegister(it) },
    ) {
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