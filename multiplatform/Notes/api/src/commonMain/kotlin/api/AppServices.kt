package api

import api.auth.AbstractAuthService
import api.data.AbstractStorageService

/**
 * Application's services holder
 */
object AppServices {

    private val authServices = mutableListOf<AbstractAuthService>()
    private var dataStoreService = mutableListOf<AbstractStorageService>()

    fun getAuthServiceByName(name: String): AbstractAuthService? {
        if (authServices.isEmpty()) return null
        for (item in authServices) {
            if (item.name == name) return item
        }
        return null
    }

    fun getDefaultAuthService(): AbstractAuthService {
        return getAuthServiceByName("firebase")!!
    }

    fun getStoreService(name: String): AbstractStorageService? {
        if (dataStoreService.isEmpty()) return null
        for (item in dataStoreService) {
            if (item.name == name) return item
        }
        return null
    }

    fun addService(
        storageService: AbstractStorageService,
    ) {
        dataStoreService.add(storageService)
    }

    fun addService(
        authService: AbstractAuthService,
    ) {
        authServices.add(authService)
    }

}
