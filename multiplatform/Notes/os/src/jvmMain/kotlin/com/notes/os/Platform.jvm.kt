package com.notes.os

import api.data.StorageOperations
import api.net.NetStateManager
import api.security.Base64Operations
import api.security.CryptoOperations
import api.security.DerivedKeyOperations
import api.utils.Log

internal actual class PlatformFactory {

    actual fun provideBase64Operations(): Base64Operations {
        TODO("Not yet implemented")
    }

    actual fun provideDerivedKeyOperations(): DerivedKeyOperations {
        TODO("Not yet implemented")
    }

    actual fun provideCryptoOperations(): CryptoOperations {
        TODO("Not yet implemented")
    }

    actual fun provideStorageOperations(): StorageOperations {
        TODO("Not yet implemented")
    }

    actual fun provideLogger(): Log {
        TODO("Not yet implemented")
    }

    actual fun provideNetStateManager(): NetStateManager {
        TODO("Not yet implemented")
    }
}