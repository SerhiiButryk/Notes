package com.notes.os

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import api.data.StorageOperations
import api.net.NetStateManager
import api.repo.Repository
import api.security.Base64Operations
import api.security.CryptoOperations
import api.security.DerivedKeyOperations
import api.utils.Log
import com.notes.os.impl.AppLogger
import com.notes.os.impl.Base64Provider
import com.notes.os.impl.CryptoProvider
import com.notes.os.impl.DerivedKeyProvider
import com.notes.os.impl.StorageProvider
import com.notes.repo.AppRepository
import java.lang.ref.WeakReference

internal actual class PlatformFactory(context: Context) {

    private val weakContextRef = WeakReference(context)

    actual fun provideBase64Operations(): Base64Operations {
        return Base64Provider()
    }

    actual fun provideDerivedKeyOperations(): DerivedKeyOperations {
        return DerivedKeyProvider()
    }

    actual fun provideCryptoOperations(): CryptoOperations {
        return CryptoProvider()
    }

    actual fun provideStorageOperations(): StorageOperations {
        val context = weakContextRef.get()
        requireNotNull(context) { "Null context" }
        return StorageProvider(context)
    }

    actual fun provideLogger(): Log {
        return AppLogger()
    }

    actual fun provideNetStateManager(): NetStateManager {
        val context = weakContextRef.get()
        requireNotNull(context) { "Null context" }
        val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return com.notes.net.NetStateManager(connectivityManager)
    }

    actual fun provideAppRepository(): Repository {
        return AppRepository.create()
    }

}

