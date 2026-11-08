package com.notes.os.impl

import androidx.datastore.core.DataStore
import androidx.datastore.core.FileStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import api.Platform
import kotlinx.coroutines.CoroutineScope
import java.io.File

actual class DatastoreFactory {
    actual val fileName: String = "app.preferences_pb"

    actual fun createDataStore(scope: CoroutineScope): DataStore<Preferences> {
        val file =
            File(Platform().getCacheDir(), fileName)
                .apply {
                    if (!exists()) createNewFile()
                }
        return createDataStore(
            scope = scope,
            storage = FileStorage(
                serializer = PreferencesFileSerializer,
                produceFile = { file },
            ),
        )
    }
}
