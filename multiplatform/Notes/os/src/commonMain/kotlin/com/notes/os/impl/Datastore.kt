package com.notes.os.impl

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Storage
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope

fun createDataStore(storage: Storage<Preferences>, scope: CoroutineScope): DataStore<Preferences> = DataStoreFactory.create(storage = storage, scope = scope)

expect class DatastoreFactory {
    val fileName: String

    fun createDataStore(scope: CoroutineScope): DataStore<Preferences>
}
