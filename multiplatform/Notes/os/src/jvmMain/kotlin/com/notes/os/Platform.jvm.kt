package com.notes.os

import api.data.Notes
import api.data.StorageOperations
import api.net.HttpClient
import api.net.NetStateInfo
import api.net.NetStateManager
import api.repo.Repository
import api.security.Base64Operations
import api.security.CryptoOperations
import api.security.DerivedKeyOperations
import api.utils.Log
import com.notes.os.impl.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

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
            override suspend fun encrypt(message: String): String {
                return message
            }

            override suspend fun decrypt(message: String): String {
                return message
            }

            override suspend fun encryptWithDerivedKey(message: String): String {
                return message
            }

            override suspend fun decryptWithDerivedKey(message: String): String {
                return message
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

            override fun getCacheDir(): String {
                return ""
            }
        }
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

    actual fun provideAppRepository(): Repository {
        return object : Repository {

            override fun getNotes(): Flow<List<Notes>> {
                return flow {
                    emit(listOf(Notes("content1", id = 1), Notes("content2", id = 2)))
                }
            }

            override fun getNotes(id: Long): Flow<Notes?> {
                return flow {
                    emit(Notes("content1", id = 1))
                }
            }

            override fun saveNote(note: Notes, onNewAdded: suspend (Long) -> Unit) {

            }

            override fun deleteNote(note: Notes, callback: (Long) -> Unit) {

            }

            override fun clear() {

            }

            override suspend fun onPasswordChanged() {

            }

            override suspend fun clearLocalAppStorage() {

            }

            override suspend fun isDataInSync(): Boolean {
                return true
            }

            override suspend fun canChangePassword(): Boolean {
                return false
            }
        }
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