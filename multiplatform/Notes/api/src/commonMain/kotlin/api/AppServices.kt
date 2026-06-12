package api

import api.AppService.Companion.DEFAULT_AUTH
import api.auth.AbstractAuthService
import api.data.AbstractStorageService
import api.data.EncryptedStore

/**
 * Generic app service definition.
 * App service is a component which exposes some specific functionality
 * which usually relies on some external APIs or even cloud service.
 */
interface AppService {

    companion object {
        const val FIREBASE_AUTH = "firebaseauth"
        const val GOOGLE_AUTH = "googlesignin"
        const val FIREBASE_STORAGE = "firebastore"
        const val GOOGLE_STORAGE = "googlestore"
        const val FIREBASE_MAIN = "firebasemain"
        const val DEFAULT_AUTH = FIREBASE_AUTH
    }

    val key: Any // Unique app service key

    fun onCreate() {}
    suspend fun onDestroy() {}
}

/**
 * Application's services holder
 */
object AppServices {

    var serverClientId = ""

    private val appServices = mutableListOf<AppService>()

    fun addService(
        appService: AppService,
    ) {
        appService.onCreate()
        if (appService is AbstractStorageService) {
            appServices.add(EncryptedStore(appService))
        } else {
            appServices.add(appService)
        }
    }

    fun getDefaultAuthService(): AbstractAuthService {
        for (service in appServices) {
            if (service.key == DEFAULT_AUTH) {
                return service as AbstractAuthService
            }
        }
        throw IllegalStateException("No installed auth default service")
    }

    fun getServiceByKey(key: Any): AppService {
        for (service in appServices) {
            if (service.key == key) {
                return service
            }
        }
        throw IllegalStateException("No installed service")
    }

    fun getAuthServiceByKey(key: Any): AbstractAuthService {
        for (service in appServices) {
            if (service.key == key) {
                return service as AbstractAuthService
            }
        }
        throw IllegalStateException("No installed auth service")
    }

    fun getStoreServices(): List<AbstractStorageService> {
        return appServices.filterIsInstance<AbstractStorageService>()
    }

    fun getStoreServicesByKey(key: Any): AbstractStorageService {
        for (service in appServices) {
            if (service.key == key) {
                return service as AbstractStorageService
            }
        }
        throw IllegalStateException("No installed store service")
    }

    /**
     * This accesses original service. Note that this will remove encryption layer !
     */
    fun __delicateCall_getOriginalServiceByKey(key: Any): AbstractStorageService {
        for (service in appServices) {
            if (service.key == key) {
                if (service is EncryptedStore) {
                    return service.delegate
                } else {
                    return service as AbstractStorageService
                }
            }
        }
        throw IllegalStateException("No installed store service")
    }

}
