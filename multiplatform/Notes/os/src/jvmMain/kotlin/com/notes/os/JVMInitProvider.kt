package com.notes.os

import api.AppServices
import api.platform
import com.notes.services.FirebaseAuthService
import com.notes.services.FirebaseService
import com.notes.ui.initResources

object JVMInitProvider {

    fun create() {
        // Perform initialization during the app launch
        val factory = PlatformFactory()
        val osPlatform = Platform(factory)
        platform = osPlatform
        initResources()
        // Set services
        AppServices.addService(FirebaseService())
        AppServices.addService(FirebaseAuthService())
    }

}