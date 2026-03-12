import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.notes.notes_ui.NotesVM
import com.notes.notes_ui.SettingsScreen
import com.notes.notes_ui.SettingsVM
import com.notes.ui.PreviewScreen
import com.notes.ui.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun EntryProviderScope<NavKey>.mainContentDestination(
    onSettingsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onBack: () -> Unit,
) {

    val scope = rememberCoroutineScope()

    entry(PreviewScreen) {

        val viewModel = viewModel { NotesVM() }

        val noteList by viewModel.notesState.collectAsState()
        val note by viewModel.noteState.collectAsState()

        NotesScreenImpl(
            onSettingsClick = onSettingsClick,
            noteList = noteList,
            note = note,
            onSelectAction = {
                scope.launch {
                    viewModel.onSelectAction(it)
                }
            },
            onAddAction = {
                scope.launch {
                    viewModel.onAddAction()
                }
            },
            tools = viewModel.richTools,
        )

    }

    entry(SettingsScreen) {

        val viewModel = viewModel { SettingsVM() }
        val accountInfo by viewModel.accountInfoState.collectAsState()
        val settings by viewModel.settingsState.collectAsState()

        LaunchedEffect(false) {
            viewModel.onOpen()
        }

        SettingsScreen(
            onBackClick = onBack,
            accountInfo = accountInfo,
            onDebugModeChanged = viewModel::onDebugChanged,
            onSignOut = viewModel::onSignOut,
            isDebugMode = settings.isDebug,
        )

    }
}
