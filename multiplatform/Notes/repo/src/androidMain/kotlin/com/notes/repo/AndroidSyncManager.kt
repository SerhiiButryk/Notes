package com.notes.repo

import android.content.Context
import android.os.Build
import android.os.FileObserver
import api.Platform
import api.data.Notes
import com.notes.db.getDatabaseInstance
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.io.File

class AndroidSyncManager(
    context: Context,
) : BaseSyncManager(getDatabaseInstance(context)) {

    private val tag = "AndroidSyncManager"

    override val notes: Flow<List<Notes>> = observeAsFlow()

    // Watch cache directory
    private fun observeAsFlow(): Flow<List<Notes>> =
        callbackFlow {

            val file = File(fileManager.secondCacheDir)

            if (!file.exists()) {
                val result = file.mkdirs()
                Platform().logger.logi("$tag::observeAsFlow: create dir ($result)")
            }

            val mask: Int =
                FileObserver.CREATE or FileObserver.DELETE or
                        FileObserver.MODIFY or FileObserver.MOVED_TO

            val observer =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    object : FileObserver(file, mask) {
                        override fun onEvent(
                            event: Int,
                            path: String?,
                        ) {
                            Platform().logger.logi("$tag::onEvent: $event")
                            scope!!.launch {
                                val notes = fileManager.readCache(file.path)
                                trySend(notes)
                            }
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    object : FileObserver(file.absolutePath, mask) {
                        override fun onEvent(
                            event: Int,
                            path: String?,
                        ) {
                            Platform().logger.logi("$tag::onEvent: $event")
                            scope!!.launch {
                                val notes = fileManager.readCache(file.path)
                                trySend(notes)
                            }
                        }
                    }
                }

            // Start watching the file system folder
            observer.startWatching()

            val notes = fileManager.readCache(file.path)
            trySend(notes)

            // Keep the Flow active. When the collector cancels or its lifecycle scope ends,
            // this block executes to clean up resources and prevent memory leaks.
            awaitClose {
                observer.stopWatching()
            }
        }

}