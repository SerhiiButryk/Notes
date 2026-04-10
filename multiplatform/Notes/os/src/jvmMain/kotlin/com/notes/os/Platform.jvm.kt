package com.notes.os

import api.data.StorageOperations
import api.net.NetStateInfo
import api.net.NetStateManager
import api.security.Base64Operations
import api.security.CryptoOperations
import api.security.DerivedKeyOperations
import api.utils.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal actual class PlatformFactory {

    actual fun provideBase64Operations(): Base64Operations {
        return object : Base64Operations {

            override fun encode(byteArray: ByteArray): String {
                return ""
            }

            override fun decode(token: String): ByteArray {
                return ByteArray(0)
            }
        }
    }

    actual fun provideDerivedKeyOperations(): DerivedKeyOperations {
        return object : DerivedKeyOperations {
            override fun generatePDKey(
                input: String,
                salt: ByteArray
            ): String {
                return ""
            }

            override fun generateSalt(): ByteArray {
                return ByteArray(0)
            }
        }
    }

    actual fun provideCryptoOperations(): CryptoOperations {
        return object : CryptoOperations {
            override fun encrypt(message: String): String {
                return ""
            }

            override fun decrypt(message: String): String {
                return ""
            }

            override suspend fun encryptWithDerivedKey(message: String): String {
                return ""
            }

            override suspend fun decryptWithDerivedKey(message: String): String {
                return ""
            }
        }
    }

    actual fun provideStorageOperations(): StorageOperations {
        return object : StorageOperations {
            override suspend fun save(value: String, key: String): Boolean {
                return true
            }

            override suspend fun get(key: String): String {
                return ""
            }

            override suspend fun clearAll() {
            }
        }
    }

    actual fun provideLogger(): Log {
        return object : Log {

            override fun logi(message: String) {

            }

            override fun loge(message: String) {

            }
        }
    }

    actual fun provideNetStateManager(): NetStateManager {
        return object : NetStateManager {
            override suspend fun isNetworkAvailable(): Boolean {
                return true
            }

            override fun observerChanges(): Flow<NetStateInfo> {
                return emptyFlow()
            }
        }
    }
}