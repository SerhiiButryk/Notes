package com.notes.app

import android.app.Application
import android.net.ConnectivityManager
import api.AppServices
import api.PlatformAPIs
import com.notes.app.data.StorageProvider
import com.notes.app.log.AppLogger
import com.notes.app.security.Base64Provider
import com.notes.app.security.CryptoProvider
import com.notes.app.security.DerivedKeyProvider
import com.notes.data.LocalNoteDatabase
import com.notes.net.NetStateManager
import com.notes.services.auth.FirebaseAuthService
import com.notes.services.auth.GoogleSignInService
import com.notes.services.storage.FirebaseFirestore
import com.notes.services.storage.GoogleDriveService
import com.notes.ui.*

class Notes : Application() {

    override fun onCreate() {
        super.onCreate()
        initComponents()
    }

    private fun initComponents() {
        // Inject dependencies.

        // Logger is initialized first as some of the below classes
        // could call it during instance creation
        PlatformAPIs.logger = AppLogger()
        PlatformAPIs.base64 = Base64Provider()
        PlatformAPIs.storage = StorageProvider(applicationContext)
        PlatformAPIs.derivedKey = DerivedKeyProvider()
        val cryptoProvider = CryptoProvider()
        PlatformAPIs.crypto = cryptoProvider

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        PlatformAPIs.netStateManager = NetStateManager(connectivityManager)

        // Icons: https://icons8.com/icons/set/cloud
        iconsCollection[h1FormatIcon] = CommonIcon(R.drawable.format_h1)
        iconsCollection[h2FormatIcon] = CommonIcon(R.drawable.format_h2)
        iconsCollection[h3FormatIcon] = CommonIcon(R.drawable.format_h3)
        iconsCollection[h4FormatIcon] = CommonIcon(R.drawable.format_h4)
        iconsCollection[h5FormatIcon] = CommonIcon(R.drawable.format_h5)
        iconsCollection[h6FormatIcon] = CommonIcon(R.drawable.format_h6)
        iconsCollection[googleIcon] = CommonIcon(R.drawable.google)
        iconsCollection[googleDriveIcon] = CommonIcon(R.drawable.googledrive)
        iconsCollection[firebaseIcon] = CommonIcon(R.drawable.firebase)
        iconsCollection[cloudSyncIcon] = CommonIcon(R.drawable.cloud_sync_icon)

        AppServices.serverClientId = getString(R.string.server_client_id)

        // Set services
        AppServices.addService(FirebaseFirestore())
        AppServices.addService(FirebaseAuthService())
        AppServices.addService(GoogleDriveService())

        val googleSignInService = GoogleSignInService()
        googleSignInService.init(applicationContext)
        AppServices.addService(googleSignInService)

        // Could be slow check if we can use coroutine or something else
        // DB init
        LocalNoteDatabase.initialize(applicationContext)
    }
}
