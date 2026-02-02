package com.notes.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import api.PlatformAPIs
import api.StorageOperations
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.lang.ref.WeakReference

class StorageProvider(
    context: Context,
) : StorageOperations {
    // Keep context as weak ref for safety
    private val contextRef = WeakReference(context)

    private val fileName = "AppSettings"

    // This should be a single instance across whole app !
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(fileName)

    override suspend fun save(
        value: String,
        key: String,
    ): Boolean {
        val ref = contextRef.get() ?: return false
        ref.applicationContext.datastore.edit { prefs ->
            val valueEnc = PlatformAPIs.crypto.encrypt(value)
            prefs[stringPreferencesKey(key)] = valueEnc
        }
        return true
    }

    override suspend fun get(key: String): String {
        val ref = contextRef.get() ?: return ""
        val result =
            ref.applicationContext.datastore.data
                .map { prefs ->
                    val valueEnc = prefs[stringPreferencesKey(key)] ?: return@map ""
                    val valueDec = PlatformAPIs.crypto.decrypt(valueEnc)
                    valueDec
                }.first()
        return result
    }

    override suspend fun clearAll() {
        val ref = contextRef.get() ?: return
        ref.applicationContext.datastore.edit { preferences ->
            preferences.clear()
        }
    }
}
