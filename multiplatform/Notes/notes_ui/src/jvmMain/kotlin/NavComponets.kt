import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.notes.notes_ui.AccountUI
import com.notes.notes_ui.SettingsUI
import com.notes.notes_ui.data.AccountInfo
import com.notes.ui.AccountInfoScreen
import com.notes.ui.PreviewScreen
import com.notes.ui.SettingsScreen

fun EntryProviderScope<NavKey>.notesMainDestination(
    onSettingsClick: () -> Unit,
    onAccountClick: () -> Unit,
) {
    entry(PreviewScreen) {
        NotesScreenImpl(onSettingsClick = onSettingsClick)
    }

    entry(AccountInfoScreen) {
        AccountUI({}, {}, {}, AccountInfo())
    }

    entry(SettingsScreen) {
        SettingsUI({}, onAccountClick, {}, {})
    }
}