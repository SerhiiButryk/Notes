package com.notes.app

import android.app.Application
import com.notes.api.Base64Operations
import com.notes.api.DerivedKeyOperations
import com.notes.api.Log
import com.notes.api.NetSettings
import com.notes.api.PlatformAPIs
import com.notes.api.StorageOperations
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class Notes : Application() {

    @Inject
    lateinit var logger: Log

    @Inject
    lateinit var base64: Base64Operations

    @Inject
    lateinit var storage: StorageOperations

    @Inject
    lateinit var derivedKey: DerivedKeyOperations

    @Inject
    lateinit var netSettings: NetSettings

    override fun onCreate() {
        super.onCreate()
        initComponents()
    }

    private fun initComponents() {

        // Setup module dependencies

        // Logger is initialized first as some of the below classes
        // could call it during instance creation
        PlatformAPIs.logger = logger
        PlatformAPIs.base64 = base64
        PlatformAPIs.storage = storage
        PlatformAPIs.derivedKey = derivedKey
        PlatformAPIs.netSettings = netSettings
    }
}