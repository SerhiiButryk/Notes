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
    private const val DEBUG_MODE = "DEBUG_MODE"
    private const val LOG_FILE_URI = "LOG_FILE_URI"

    @OptIn(ExperimentalAtomicApi::class)
    private val code = AtomicReference("")

    var editorBackEnabled = true
    var attachmentsEnabled = true

    @Volatile // Make sure that all thread will see the updated value
    var isDebugEnabled = false

    @Volatile // Make sure that all thread will see the updated value
    var isDarkThemeEnabled = false

    @Volatile
    var logfilePath: String = ""

    suspend fun loadAppSettings() {

        val debug = Platform().storage.get(key = DEBUG_MODE)
        if (debug.isNotEmpty()) {
            isDebugEnabled = debug.toBoolean()
        }

        val theme = Platform().storage.get(key = USER_THEME)
        if (theme.isEmpty()) {
            isDarkThemeEnabled = false
        }
        isDarkThemeEnabled = theme.toBoolean()

        val logFilePath = Platform().storage.get(key = LOG_FILE_URI)
        logfilePath = logFilePath
    }

    suspend fun clearUserEmail() {
        Platform().storage.save(value = "", key = REGISTERED_USER_EMAIL)
    }

    suspend fun setUserEmail(email: String) {
        Platform().storage.save(value = email, key = REGISTERED_USER_EMAIL)
    }

    suspend fun getUserEmail(): String =
        Platform().storage.get(key = REGISTERED_USER_EMAIL)

    suspend fun setTheme(isDark: Boolean) {
        isDarkThemeEnabled = isDark
        Platform().storage.save(value = isDark.toString(), key = USER_THEME)
    }

    suspend fun setDebugMode(enable: Boolean) {
        isDebugEnabled = enable
        Platform().storage.save(value = "$enable", key = DEBUG_MODE)
    }

    suspend fun setLogFilePath(value: String) {
        logfilePath = value
        Platform().storage.save(value = value, key = LOG_FILE_URI)
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
