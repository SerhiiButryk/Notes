package com.notes.app.di

import com.notes.app.data.StorageProvider
import com.notes.app.log.AppLogger
import com.notes.app.net.LocalNetSettings
import com.notes.app.security.Base64Provider
import com.notes.interfaces.Base64Operations
import com.notes.interfaces.DerivedKeyOperations
import com.notes.interfaces.Log
import com.notes.interfaces.NetSettings
import com.notes.interfaces.StorageOperations
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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

    }

}