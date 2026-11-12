import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar

@Composable
fun FrameWindowScope.Menu(
    appScope: ApplicationScope,
    menuState: MutableState<MenuState>
) {
    MenuBar {
        Menu("File", mnemonic = 'F') {
            Item(
                "Settings",
                onClick = {
                    menuState.value = menuState.value.copy(
                        isShown = true,
                        activeMenuItem = MenuState.SETTINGS_MENU,
                    )
                },
            )
        }
    }
}