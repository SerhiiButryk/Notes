package api.data

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

    @OptIn(ExperimentalAtomicApi::class)
    private val _canUse = AtomicBoolean(false)

    @OptIn(ExperimentalAtomicApi::class)
    var canUse: Boolean
        get() {
            return _canUse.load()
        }
    set(value) {
        _canUse.store(value)
    }

}