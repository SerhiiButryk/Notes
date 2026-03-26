package com.notes.os

import api.OSPlatform
import api.data.StorageOperations
import api.net.NetStateManager
import api.security.Base64Operations
import api.security.CryptoOperations
import api.security.DerivedKeyOperations
import api.utils.Log

internal expect class PlatformFactory {
    fun provideBase64Operations(): Base64Operations
    fun provideDerivedKeyOperations(): DerivedKeyOperations
    fun provideCryptoOperations(): CryptoOperations
    fun provideStorageOperations(): StorageOperations
    fun provideLogger(): Log
    fun provideNetStateManager(): NetStateManager
}

internal class Platform(factory: PlatformFactory) : OSPlatform {

    override val base64: Base64Operations = factory.provideBase64Operations()

    override val storage: StorageOperations = factory.provideStorageOperations()

    override val derivedKey: DerivedKeyOperations = factory.provideDerivedKeyOperations()

    override val logger: Log = factory.provideLogger()

    override val crypto: CryptoOperations = factory.provideCryptoOperations()

    override val netStateManager: NetStateManager = factory.provideNetStateManager()

}

