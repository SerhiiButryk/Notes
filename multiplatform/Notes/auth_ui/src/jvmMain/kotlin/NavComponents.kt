import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.notes.auth_ui.AuthVM
import com.notes.auth_ui.ui.OnBoardingUIImpl
import com.notes.ui.AlertDialogUI
import com.notes.ui.LoginScreen
import com.notes.ui.OnBoardingNoteScreen
import com.notes.ui.RegistrationScreen
import kotlinx.coroutines.launch

@Composable
fun EntryProviderScope<NavKey>.authDestination(
    onNavLogin: () -> Unit,
    onNavRegister: () -> Unit,
    onNavOnBoarding: () -> Unit,
    viewModel: AuthVM,
) {

    val scope = rememberCoroutineScope()

    entry(LoginScreen) {

        val uiState by viewModel.loginUIState.collectAsState()

        LoginScreenImpl(
            onLogin = {
                viewModel.login(state = it, onSuccess = onNavLogin)
            },
            state = uiState,
            title = uiState.title,
            subTitle = uiState.subtitle,
        )
    }

    entry(RegistrationScreen) {

        val uiState by viewModel.registerUIState.collectAsState()

        RegisterScreenImpl(
            onLogin = {
                scope.launch {
                    viewModel.onShowLoginScreen()
                }
                onNavRegister()
            },
            onRegister = {
                viewModel.register(state = it, onSuccess = onNavRegister)
            },
            state = uiState,
            title = uiState.title,
            subTitle = uiState.subtitle,
        )
    }

    entry(OnBoardingNoteScreen) {
        OnBoardingUIImpl(
            onContinue = {
                scope.launch {
                    viewModel.onOnBoardingContinue()
                }
                onNavOnBoarding()
            },
            modifier = Modifier.widthIn(max = 800.dp),
        )
    }

    Dialog(viewModel)

}

@Composable
private fun Dialog(viewModel: AuthVM) {
    val dialogState = viewModel.dialogState.collectAsState()
    val dialogValue = dialogState.value
    if (dialogValue != null) {
        AlertDialogUI(
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirmation = {
                viewModel.dismissDialog()
                dialogValue.onConfirm?.invoke()
            },
            dialogTitle = dialogValue.title,
            dialogText = dialogValue.subtitle,
        )
    }
}