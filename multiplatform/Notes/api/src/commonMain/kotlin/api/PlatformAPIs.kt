package api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Platform specific operations for other components
 */
object PlatformAPIs {
    lateinit var base64: Base64Operations
    lateinit var storage: StorageOperations
    lateinit var derivedKey: DerivedKeyOperations
    lateinit var logger: Log
    lateinit var crypto: CryptoOperations

    var netStateManager: NetStateManager = object : NetStateManager {
        override suspend fun isNetworkAvailable(): Boolean {
            return true
        }

        override fun observerChanges(): Flow<NetStateInfo> {
            return flow {}
        }
    }
}
