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
}

internal class Platform(factory: PlatformFactory) : OSPlatform {

    override val base64: Base64Operations = factory.provideBase64Operations()

    override val storage: StorageOperations = factory.provideStorageOperations()

    override val logger: Log = factory.provideLogger()

    override val crypto: CryptoOperations = factory.provideCryptoOperations()

    override val netStateManager: NetStateManager = factory.provideNetStateManager()

    override val appRepo: BaseRepo = factory.provideAppRepository()

    override val httpClient: HttpClient = factory.provideHttpClient()
}

