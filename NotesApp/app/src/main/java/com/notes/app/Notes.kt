package com.notes.app

import android.app.Application
import com.notes.app.data.StorageProvider
import com.notes.app.security.Base64Provider
import com.notes.interfaces.PlatformAPIs
import com.notes.interfaces.DerivedKeyOperations
import com.notes.interfaces.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Notes : Application() {

    override fun onCreate() {
        super.onCreate()
        initComponents()
    }

    fun initComponents() {

        // Set platform component implementation for modules

        PlatformAPIs.base64 = Base64Provider()
        PlatformAPIs.storage = StorageProvider()
        PlatformAPIs.derivedKey = object : DerivedKeyOperations {

            override fun generatePDKey(input: String, salt: ByteArray): String {
                return com.notes.app.security.generatePDKey(input, salt)
            }

            override fun generateSalt(): ByteArray {
                return com.notes.app.security.generateSalt()
            }
        }

        PlatformAPIs.log = object : Log {
            override fun log(message: String) {
                android.util.Log.i("Notes", message)
            }
        }
    }
}