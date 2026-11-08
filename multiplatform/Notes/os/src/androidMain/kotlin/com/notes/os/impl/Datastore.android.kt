package com.notes.os.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope

actual class DatastoreFactory {
    actual val fileName: String
        get() = TODO("Not yet implemented")

    actual fun createDataStore(scope: CoroutineScope): DataStore<Preferences> {
        TODO("Not yet implemented")
    }
}
