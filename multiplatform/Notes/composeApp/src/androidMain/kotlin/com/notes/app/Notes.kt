package com.notes.app

import android.app.Application
import android.content.Context
import api.AppServices
import api.PlatformAPIs
import com.notes.app.data.StorageProvider
import com.notes.app.log.AppLogger
import com.notes.app.security.Base64Provider
import com.notes.app.security.CryptoProvider
import com.notes.app.security.DerivedKeyProvider
import com.notes.services.auth.FirebaseAuthService
import com.notes.services.storage.FirebaseFirestore
import api.ui.CommonIcons
import com.notes.services.auth.GoogleSignInService
import com.notes.services.storage.EncryptedStore
import com.notes.services.storage.GoogleDriveService

class Notes : Application() {
    override fun onCreate() {
        super.onCreate()
        initComponents(applicationContext)
    }

    private fun initComponents(context: Context) {
        // Inject dependencies.

        // Logger is initialized first as some of the below classes
        // could call it during instance creation
        PlatformAPIs.logger = AppLogger()
        PlatformAPIs.base64 = Base64Provider()
        PlatformAPIs.storage = StorageProvider(context)
        PlatformAPIs.derivedKey = DerivedKeyProvider()
        val cryptoProvider = CryptoProvider()
        PlatformAPIs.crypto = cryptoProvider

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

        // Set services
        AppServices.addService(EncryptedStore(FirebaseFirestore()))
        AppServices.addService(FirebaseAuthService())
        AppServices.addService(GoogleDriveService())

        val googleSignInService = GoogleSignInService()
        googleSignInService.init(applicationContext)
        AppServices.addService(googleSignInService)
    }
}
