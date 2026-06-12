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
import com.notes.repo.AppRepoCommon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal actual class PlatformFactory {

    actual fun provideBase64Operations(): Base64Operations {
        return Base64Provider()
    }

    actual fun provideCryptoOperations(): CryptoOperations {
        return CryptoProvider()
    }

    actual fun provideStorageOperations(): StorageOperations {
        return StorageProvider()
    }

    actual fun provideLogger(): Log {
        return AppLogger()
    }

    actual fun provideNetStateManager(): NetStateManager {
        return object : NetStateManager {
            override suspend fun isNetworkAvailable(): Boolean {
                return true
            }

            override fun observerChanges(): Flow<NetStateInfo> {
                return emptyFlow()
            }

            override fun startObserver() {

            }
        }
    }

    actual fun provideAppRepository(): BaseRepo {
        return AppRepoCommon()
    }

    actual fun provideHttpClient(): HttpClient {
        return object : HttpClient {
            override suspend fun post(
                url: String,
                formArgs: Map<String, String>
            ): String? {
                return null
            }

            override fun postSync(
                url: String,
                formArgs: Map<String, String>
            ): String? {
                return null
            }
        }
    }
}