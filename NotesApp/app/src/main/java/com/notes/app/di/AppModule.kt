package com.notes.app.di

import com.notes.api.AuthService
import com.notes.app.data.StorageProvider
import com.notes.app.log.AppLogger
import com.notes.app.net.LocalNetSettings
import com.notes.app.security.Base64Provider
import com.notes.api.Base64Operations
import com.notes.api.CryptoOperations
import com.notes.api.DerivedKeyOperations
import com.notes.api.Log
import com.notes.api.NetSettings
import com.notes.api.PlatformAPIs
import com.notes.api.StorageOperations
import com.notes.app.security.CryptoProvider
import com.notes.notes_ui.Repository
import com.notes.notes_ui.data.OfflineRepository
import com.notes.services.auth.FirebaseAuthService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun bindLogger(logger: AppLogger): Log

    @Binds
    fun bindBase64Provider(base64Operations: Base64Provider): Base64Operations

    @Binds
    fun bindStorageProvider(storageProvider: StorageProvider): StorageOperations

    @Binds
    fun bindNetSettings(localNetSettings: LocalNetSettings): NetSettings

    companion object {

        @Provides
        fun provideDerivedKeyOperations(): DerivedKeyOperations {
            return object : DerivedKeyOperations {

                override fun generatePDKey(input: String, salt: ByteArray): String {
                    return com.notes.app.security.generatePDKey(input, salt)
                }

                override fun generateSalt(): ByteArray {
                    return com.notes.app.security.generateSalt()
                }
            }
        }

        @Provides
        fun provideAuthService(): AuthService {
            return FirebaseAuthService()
        }

        @Provides
        fun provideRepo(): Repository {
            return OfflineRepository()
        }

        @Singleton
        @Provides
        fun provideCryptoOperations(log: Log): CryptoOperations {
            // Init as we use it early
            PlatformAPIs.logger = log
            // Single instance should be created
            val provider = CryptoProvider()
            return object : CryptoOperations {
                @Synchronized // Underlined KeyStore is not thread-safe
                override fun encrypt(message: String): String {
                    return provider.encrypt(message).message
                }
                @Synchronized // Underlined KeyStore is not thread-safe
                override fun decrypt(message: String): String {
                    return provider.decrypt(message).message
                }
            }
        }

    }

}