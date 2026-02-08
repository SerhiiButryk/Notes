package com.notes.app

import android.app.Application
import android.net.ConnectivityManager
import api.AppServices
import api.PlatformAPIs
import api.ui.CommonIcons
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

        // Icons came from - https://icons8.com/icons/set/cloud
        // Looks like we can't access R class in shared module
        // which is a bit strange, so to work around this
        // I added this class
        CommonIcons.h1FormatIcon = R.drawable.format_h1
        CommonIcons.h2FormatIcon = R.drawable.format_h2
        CommonIcons.h3FormatIcon = R.drawable.format_h3
        CommonIcons.h4FormatIcon = R.drawable.format_h4
        CommonIcons.h5FormatIcon = R.drawable.format_h5
        CommonIcons.h6FormatIcon = R.drawable.format_h6
        CommonIcons.addIcon = R.drawable.add
        CommonIcons.redoIcon = R.drawable.redo
        CommonIcons.undoIcon = R.drawable.undo
        CommonIcons.replaceIcon = R.drawable.replace
        CommonIcons.replaceAllIcon = R.drawable.replace_all
        CommonIcons.syncIcon = R.drawable.sync
        CommonIcons.googleIcon = R.drawable.google
        CommonIcons.googleDriveIcon = R.drawable.googledrive
        CommonIcons.firebaseIcon = R.drawable.firebase
        CommonIcons.cloudSyncIcon = R.drawable.cloud_sync_icon

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
