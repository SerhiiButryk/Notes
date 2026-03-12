package com.notes.os

import api.AppServices
import api.Platform
import api.data.AppSettings
import api.platform
import com.notes.os.impl.crypto.TinkCrypto
import com.notes.services.Firebase
import com.notes.ui.initResources
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object JVMInitProvider {
    fun onCreate() {

        // Perform initialization during the app launch
        val factory = PlatformFactory()
        val osPlatform = Platform(factory)
        platform = osPlatform
        initResources()

        // Set services
        val firebase = Firebase()
        AppServices.addService(firebase)
        AppServices.addService(firebase.createAuthService())
        AppServices.addService(firebase.createFirestoreService())

        // Init crypto lib
        TinkCrypto.getCryptoHandle()

        // Other app configs
        AppSettings.editorBackEnabled = false
        AppSettings.attachmentsEnabled = false
        AppSettings.isDebugEnabled = true
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onDestroy() {
        GlobalScope.launch {
            AppServices.clear()
            Platform().logger.close()
        }
    }

}
