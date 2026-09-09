package api.data

import api.Platform
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * This class isolates application specific settings which are set by a platform or user
 */
object AppSettings {

    private const val REGISTERED_USER_EMAIL = "REGISTERED_USER_EMAIL"
    private const val USER_THEME = "USER_THEME"

    @OptIn(ExperimentalAtomicApi::class)
    private val code = AtomicReference("")

    var editorBackEnabled = true
    var attachmentsEnabled = true

    @Volatile // Make sure that all thread will see the updated value
    var isDebugEnabled = true

    @Volatile // Make sure that all thread will see the updated value
    var isDarkThemeEnabled = false

    suspend fun setUserEmail(email: String) {
        Platform().storage.save(email, REGISTERED_USER_EMAIL)
    }

    suspend fun getUserEmail(): String =
        Platform().storage.get(REGISTERED_USER_EMAIL)

    suspend fun setTheme(isDark: Boolean) {
        isDarkThemeEnabled = isDark
        Platform().storage.save(isDark.toString(), USER_THEME)
    }

    suspend fun isDarkTheme(): Boolean {
        val value = Platform().storage.get(USER_THEME)
        if (value.isEmpty()) {
            isDarkThemeEnabled = false
            return isDarkThemeEnabled
        }
        isDarkThemeEnabled = value.toBoolean()
        return isDarkThemeEnabled
    }

    @OptIn(ExperimentalAtomicApi::class)
    fun cacheCode(code: String) {
        this.code.store(code)
    }

    @OptIn(ExperimentalAtomicApi::class)
    fun getCode(): String {
        return this.code.load()
    }

}
