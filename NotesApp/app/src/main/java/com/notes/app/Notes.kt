package com.notes.app

import android.app.Application
import com.notes.app.data.StorageProvider
import com.notes.app.net.LocalNetSettings
import com.notes.app.security.Base64Provider
import com.notes.interfaces.PlatformAPIs
import com.notes.interfaces.DerivedKeyOperations
import com.notes.interfaces.Log
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference

@HiltAndroidApp
class Notes : Application() {

    override fun onCreate() {
        super.onCreate()
        initComponents()
    }

    fun initComponents() {

        // Set platform component implementation for modules

        // Logger is initialized first as some of the below classes
        // could call it during instance creation
        PlatformAPIs.logger = object : Log {

            override fun logi(message: String) {
                android.util.Log.i("Notes", message)
            }

            override fun loge(message: String) {
                android.util.Log.e("Notes", message)
            }
        }

        PlatformAPIs.base64 = Base64Provider()
        // Keep context as weak ref for safety
        PlatformAPIs.storage = StorageProvider(contextRef = WeakReference(this))
        PlatformAPIs.derivedKey = object : DerivedKeyOperations {

            override fun generatePDKey(input: String, salt: ByteArray): String {
                return com.notes.app.security.generatePDKey(input, salt)
            }

            override fun generateSalt(): ByteArray {
                return com.notes.app.security.generateSalt()
            }
        }

        PlatformAPIs.netSettings = LocalNetSettings(this)
    }
}