package com.notes.os

import android.content.Context
import androidx.startup.Initializer
import api.AppServices
import api.Platform
import api.data.AppSettings
import api.platform
import com.notes.services.auth.GoogleSignInService
import com.notes.services.createFirebaseAuthService
import com.notes.services.createFirebaseFirestoreService
import com.notes.services.storage.GoogleDriveService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class AndroidInitProvider : Initializer<Platform> {

    @OptIn(DelicateCoroutinesApi::class)
    override fun create(context: Context): Platform {
        // Perform initialization during the app launch

        val factory = PlatformFactory(context)
        val osPlatform = Platform(factory)
        platform = osPlatform

        Platform().logger.init(context)

        // This is fast and should not cause any issues
        GlobalScope.launch {
            AppSettings.loadAppSettings()
        }

        // Set services
        AppServices.addService(createFirebaseAuthService())
        AppServices.addService(createFirebaseFirestoreService())
        AppServices.addService(GoogleDriveService())

        val googleSignInService = GoogleSignInService()
        googleSignInService.init(context.applicationContext)
        AppServices.addService(googleSignInService)

        Platform().logger.logi("App launched >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")

        Platform().netStateManager.startObserver()

        return osPlatform
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()
}
