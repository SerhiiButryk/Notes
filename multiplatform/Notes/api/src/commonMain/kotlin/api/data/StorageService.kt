package api.data

import api.AppServices
import api.Platform
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

interface StorageService {

    val name: String

    suspend fun store(document: Document): Boolean

    suspend fun load(name: String): Document?

    suspend fun delete(name: String): Boolean

    suspend fun fetchAll(): List<Document>
}

abstract class AbstractStorageService : StorageService {

    private val tag = "AbstractStorageService"

    @OptIn(ExperimentalAtomicApi::class)
    private val _canUse = AtomicBoolean(false)

    @OptIn(ExperimentalAtomicApi::class)
    open var canUse: Boolean
        get() {
            return _canUse.load()
        }
    set(value) {
        _canUse.store(value)
    }

    protected fun paramsCheck(name: String): Boolean {

        if (!isAuthenticated()) return false

        if (name.isEmpty()) {
            Platform().logger.loge("$tag::paramsCheck() stop cos empty data")
            return false
        }

        try {
            val nameCheck = name.toInt()
            if (nameCheck < 0) {
                Platform().logger.loge("$tag::paramsCheck() stop cos not valid name")
                return false
            }
        } catch (e: NumberFormatException) {
            Platform().logger.loge("$tag::paramsCheck() stop cos not a number")
            return false
        }

        return true
    }

    protected fun isAuthenticated(): Boolean {
        val authService = AppServices.getDefaultAuthService()
        val uid = authService?.getUserId()

        if (authService == null || !authService.isAuthenticated()) {
            Platform().logger.loge("$tag::isAuthenticated() not authenticated")
            return false
        }

        if (uid.isNullOrEmpty()) {
            Platform().logger.loge("$tag::isAuthenticated() uid is invalid")
            return false
        }

        return true
    }

}