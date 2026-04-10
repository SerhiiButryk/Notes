package com.notes.os

import api.platform
import com.notes.ui.initResources

object JVMInitProvider {

    fun create() {

        // Perform initialization during the app launch

        val factory = PlatformFactory()
        val osPlatform = Platform(factory)
        platform = osPlatform

        initResources()

    }

}