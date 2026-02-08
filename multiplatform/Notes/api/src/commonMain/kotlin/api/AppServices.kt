package api

import api.auth.AbstractAuthService
import api.data.AbstractStorageService
import api.data.EncryptedStore

/**
 * Application's services holder
 */
object AppServices {

    var serverClientId = ""
    private val authServices = mutableListOf<AbstractAuthService>()
    val dataStoreService = mutableListOf<AbstractStorageService>()

    fun getAuthServiceByName(name: String): AbstractAuthService? {
        if (authServices.isEmpty()) return null
        for (item in authServices) {
            if (item.name == name) return item
        }
        return null
    }

    fun getDefaultAuthService(): AbstractAuthService? {
        return getAuthServiceByName("firebase")
    }

    fun getStoreService(name: String): AbstractStorageService? {
        if (dataStoreService.isEmpty()) return null
        for (item in dataStoreService) {
            if (item.name == name) return item
        }
        return null
    }

    /**
     * This accesses original service. Note that this will remove encryption layer !
     */
    fun __delicateCall_getOriginalStoreService(name: String): AbstractStorageService? {
        if (dataStoreService.isEmpty()) return null
        for (item in dataStoreService) {
            if (item.name == name) {
                if (item is EncryptedStore) {
                    return item.delegate
                } else {
                    return item
                }
            }
        }
        return null
    }

    fun addService(
        storageService: AbstractStorageService,
    ) {
        dataStoreService.add(EncryptedStore(storageService))
    }

    fun addService(
        authService: AbstractAuthService,
    ) {
        authServices.add(authService)
    }

}
