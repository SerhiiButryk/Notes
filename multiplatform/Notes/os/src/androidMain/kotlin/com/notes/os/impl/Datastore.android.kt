package com.notes.os.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual class DatastoreFactory {
    actual val fileName: String
        get() = TODO("Not yet implemented")

    actual fun createDataStore(): DataStore<Preferences> {
        TODO("Not yet implemented")
    }
}
