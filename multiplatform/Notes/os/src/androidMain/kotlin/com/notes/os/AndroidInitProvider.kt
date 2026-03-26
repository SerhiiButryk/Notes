package com.notes.os

import android.content.Context
import androidx.startup.Initializer
import api.AppServices
import api.platform
import com.notes.data.LocalNoteDatabase
import com.notes.services.auth.FirebaseAuthService
import com.notes.services.auth.GoogleSignInService
import com.notes.services.storage.FirebaseFirestore
import com.notes.services.storage.GoogleDriveService

internal class AndroidInitProvider : Initializer<Platform> {

    override fun create(context: Context): Platform {

        // Perform important initialization

        val factory = PlatformFactory(context)
        val osPlatform = Platform(factory)
        platform = osPlatform

        // Set services
        AppServices.addService(FirebaseFirestore())
        AppServices.addService(FirebaseAuthService())
        AppServices.addService(GoogleDriveService())

        val googleSignInService = GoogleSignInService()
        googleSignInService.init(context.applicationContext)
        AppServices.addService(googleSignInService)

        // Need to check if it's slow or not
        LocalNoteDatabase.initialize(context.applicationContext)

        return osPlatform
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }
}