package api.data

import api.Platform
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * This class isolates application specific settings which are set by a platform
 */
object AppSettings {

    private const val REGISTERED_USER_EMAIL = "REGISTERED_USER_EMAIL"

    @OptIn(ExperimentalAtomicApi::class)
    private val code = AtomicReference("")

    var editorBackEnabled = true
    var attachmentsEnabled = true

    @Volatile // Make sure that all thread will see the updated value
    var isDebugEnabled = true

    suspend fun setUserEmail(email: String) {
        Platform().storage.save(email, REGISTERED_USER_EMAIL)
    }

    suspend fun getUserEmail(): String =
        Platform().storage.get(REGISTERED_USER_EMAIL)

    @OptIn(ExperimentalAtomicApi::class)
    fun cacheCode(code: String) {
        this.code.store(code)
    }

    @OptIn(ExperimentalAtomicApi::class)
    fun getCode(): String {
        return this.code.load()
    }

}
