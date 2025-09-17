package com.notes.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.notes.app.security.CryptoProvider
import com.notes.api.PlatformAPIs.logger
import com.notes.api.StorageOperations
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.ref.WeakReference
import javax.inject.Inject

class StorageProvider @Inject constructor(@ApplicationContext context: Context) :
    StorageOperations {

    // Keep context as weak ref for safety
    private val contextRef = WeakReference(context)

    private val fileName = "AppSettings"
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(fileName)

    // Provider is created lazily because it depends on logger which is not available at the
    // moment of instance creation. Don't want to inject logger separately for now.
    private var cryptoProvider: CryptoProvider? = null
    // To guard cryptoProvider
    private val mutex = Mutex()

    override suspend fun save(value: String, key: String): Boolean {
        ensureCryptoProvidedIsCreated()
        val ref = contextRef.get() ?: return false
        ref.datastore.edit { prefs ->
            val valueEnc = cryptoProvider!!.encrypt(value).message
            prefs[stringPreferencesKey(key)] = valueEnc
        }
        return true
    }

    override suspend fun get(key: String): String {
        ensureCryptoProvidedIsCreated()
        val ref = contextRef.get() ?: return ""
        val result =
            ref.datastore.data.map { prefs ->
                val valueEnc = prefs[stringPreferencesKey(key)] ?: return@map ""
                val valueDec = cryptoProvider!!.decrypt(valueEnc).message
                valueDec
            }.first()
        return result
    }

    private suspend fun ensureCryptoProvidedIsCreated() {
        if (cryptoProvider == null) {
            mutex.withLock {
                cryptoProvider = CryptoProvider()
                logger.logi("ensureCryptoProvidedIsCreated() object is created")
            }
        }
    }
}