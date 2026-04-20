import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.notes.auth_ui.AuthVM
import com.notes.auth_ui.ui.OnBoardingUIImpl
import com.notes.ui.LoginScreen
import com.notes.ui.OnBoardingNoteScreen
import com.notes.ui.RegistrationScreen

@Composable
fun EntryProviderScope<NavKey>.authDestination(
    onNavLogin: () -> Unit,
    onNavRegister: () -> Unit,
    onNavContinue: () -> Unit
) {

    val viewModel = viewModel { AuthVM() }

    entry(LoginScreen) {

        val uiState by viewModel.loginUIState.collectAsState()

        LoginScreenImpl(
            onLogin = {
                viewModel.login(state = it, onSuccess = onNavLogin)
            },
            state = uiState
        )
    }

    entry(RegistrationScreen) {

        val uiState by viewModel.registerUIState.collectAsState()

        RegisterScreenImpl(
            onLogin = {
                onNavRegister()
            },
            onRegister = {
                viewModel.register(state = it, onSuccess = onNavRegister)
            },
            state = uiState
        )
    }

    entry(OnBoardingNoteScreen) {
        OnBoardingUIImpl(
            onContinue = onNavContinue,
            modifier = Modifier.widthIn(max = 800.dp),
        )
    }

}