package com.notes.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.notes.app.security.CryptoProvider
import com.notes.interfaces.StorageOperations
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.lang.ref.WeakReference

class StorageProvider(private val contextRef: WeakReference<Context>) : StorageOperations {

    private val fileName = "AppSettings"
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(fileName)

    private val cryptoProvider = CryptoProvider()

    override suspend fun save(value: String, key: String): Boolean {
        val ref = contextRef.get() ?: return false
        ref.datastore.edit { prefs ->
            val valueEnc = cryptoProvider.encrypt(value).message
            prefs[stringPreferencesKey(key)] = valueEnc
        }
        return true
    }

    override suspend fun get(key: String): String {
        val ref = contextRef.get() ?: return ""
        val result =
            ref.datastore.data.map { prefs ->
                val valueEnc = prefs[stringPreferencesKey(key)] ?: return@map ""
                val valueDec = cryptoProvider.decrypt(valueEnc).message
                valueDec
            }.first()
        return result
    }
}