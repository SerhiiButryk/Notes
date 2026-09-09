data class MenuState(val isShown: Boolean = false, val activeMenuItem: Int = NONE) {
    companion object {
        const val NONE = 0
        const val SETTINGS_MENU = 1
    }
}