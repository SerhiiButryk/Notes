package com.notes.os

import api.OSPlatform
import api.data.StorageOperations
import api.net.HttpClient
import api.net.NetStateManager
import api.repo.BaseRepo
import api.security.Base64Operations
import api.security.CryptoOperations
import api.utils.Log

internal expect class PlatformFactory {
    fun provideBase64Operations(): Base64Operations

    fun provideCryptoOperations(): CryptoOperations

    fun provideStorageOperations(): StorageOperations

    fun provideLogger(): Log

    fun provideNetStateManager(): NetStateManager

    fun provideAppRepository(): BaseRepo

    fun provideHttpClient(): HttpClient

    fun provideCacheDirPath(): String

    fun provideRootFilesDir(): String
}

internal class Platform(
    private val factory: PlatformFactory,
) : OSPlatform {
    override val base64: Base64Operations by lazy { factory.provideBase64Operations() }

    override val storage: StorageOperations by lazy { factory.provideStorageOperations() }

    override val logger: Log by lazy { factory.provideLogger() }

    override val crypto: CryptoOperations by lazy { factory.provideCryptoOperations() }

    override val netStateManager: NetStateManager by lazy { factory.provideNetStateManager() }

    override val appRepo: BaseRepo by lazy { factory.provideAppRepository() }

    override val httpClient: HttpClient by lazy { factory.provideHttpClient() }

    override fun getCacheDir() = factory.provideCacheDirPath()

    override fun getRootFilesDir() = factory.provideRootFilesDir()
}
