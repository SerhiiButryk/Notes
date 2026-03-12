package com.notes.os.impl

import androidx.datastore.core.DataStore
import androidx.datastore.core.FileStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import api.Platform
import java.io.File

actual class DatastoreFactory {
    actual val fileName: String = "app.preferences_pb"

    actual fun createDataStore(): DataStore<Preferences> {
        val file =
            File(Platform().getCacheDir(), fileName)
                .apply {
                    if (!exists()) createNewFile()
                }
        return createDataStore(
            FileStorage(
                serializer = PreferencesFileSerializer,
                produceFile = { file },
            ),
        )
    }
}
