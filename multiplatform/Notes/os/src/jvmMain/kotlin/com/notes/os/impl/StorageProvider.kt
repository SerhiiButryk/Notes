package com.notes.os.impl

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import api.Platform
import api.data.StorageOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first

class StorageProvider : StorageOperations {

    companion object {
        @Volatile
        private var scope = CoroutineScope(Dispatchers.IO)
        @Volatile
        private var dataStore = DatastoreFactory().createDataStore(scope = scope)
    }

    override suspend fun save(
        value: String,
        key: String,
    ): Boolean {
        val valueEnc = Platform().crypto.encrypt(value)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key.hashCode().toString())] = valueEnc
        }
        return true
    }

    override suspend fun get(key: String): String {
        val prefs = dataStore.data.first()
        val valueEnc = prefs[stringPreferencesKey(key.hashCode().toString())] ?: ""
        if (valueEnc.isNotEmpty()) {
            return Platform().crypto.decrypt(valueEnc)
        }
        return valueEnc
    }

    override suspend fun clearAll() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
        // This is a workaround to make sure that at this point the prefs are cleared.
        // The 'dataStore.data' still returns old data after the 'prefs.clear()' call.
        scope.cancel()
        scope = CoroutineScope(Dispatchers.IO)
        dataStore = DatastoreFactory().createDataStore(scope = scope)
    }
}
