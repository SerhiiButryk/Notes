package com.notes.os

import api.data.StorageOperations
import api.net.HttpClient
import api.net.NetStateInfo
import api.net.NetStateManager
import api.repo.BaseRepo
import api.security.Base64Operations
import api.security.CryptoOperations
import api.utils.Log
import com.notes.os.impl.AppLogger
import com.notes.os.impl.Base64Provider
import com.notes.os.impl.CryptoProvider
import com.notes.os.impl.StorageProvider
import com.notes.repo.AppRepoBase
import com.notes.repo.JvmSyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.io.File

internal actual class PlatformFactory {
    actual fun provideBase64Operations(): Base64Operations = Base64Provider()

    actual fun provideCryptoOperations(): CryptoOperations = CryptoProvider()

    actual fun provideStorageOperations(): StorageOperations = StorageProvider()

    actual fun provideLogger(): Log = AppLogger()

    actual fun provideNetStateManager(): NetStateManager =
        object : NetStateManager {
            override suspend fun isNetworkAvailable(): Boolean = true

            override fun observerChanges(): Flow<NetStateInfo> = emptyFlow()

            override fun startObserver() {
            }
        }

    actual fun provideAppRepository(): BaseRepo =
        AppRepoBase(syncManager = JvmSyncManager())

    actual fun provideHttpClient(): HttpClient =
        object : HttpClient {
            override suspend fun post(
                url: String,
                formArgs: Map<String, String>,
            ): String? = null

            override fun postSync(
                url: String,
                formArgs: Map<String, String>,
            ): String? = null
        }

    actual fun provideCacheDirPath(): String {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".notes").apply { mkdirs() }
        return appDir.absolutePath
    }

    actual fun provideRootFilesDir(): String = ""
}
