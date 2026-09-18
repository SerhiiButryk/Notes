package com.notes.repo

import android.content.Context
import android.os.Build
import android.os.FileObserver
import api.Platform
import api.data.Notes
import com.notes.db.getDatabaseInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class AndroidSyncManager(
    context: Context,
) : BaseSyncManager(getDatabaseInstance(context)) {

    private val tag = "AndroidSyncManager"

    private val sharedFlow = MutableSharedFlow<List<Notes>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val notes: Flow<List<Notes>> = sharedFlow.asSharedFlow()

    override suspend fun store(
        notes: List<Notes>,
        forceOverride: Boolean,
        scope: CoroutineScope
    ) {
        Platform().logger.logi("$tag::store()")
        super.store(notes, forceOverride, scope)
        sharedFlow.emit(notes)
    }

    override fun startCacheDirWatching(scope: CoroutineScope?) {

        val file = File(fileManager.secondCacheDir)

        if (!file.exists()) {
            file.mkdirs()
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
                        Platform().logger.logd("$tag::onEvent: $event")
                        require(scope != null)
                        scope.launch {
                            val notes = fileManager.readCache(file.path)
                            sharedFlow.emit(notes)
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
                        Platform().logger.logd("$tag::onEvent: $event")
                        require(scope != null)
                        scope.launch {
                            val notes = fileManager.readCache(file.path)
                            sharedFlow.emit(notes)
                        }
                    }
                }
            }

        // Start watching the file system folder
        observer.startWatching()

        require(scope != null)
        scope.launch {
            val notes = fileManager.readCache(file.path)
            sharedFlow.emit(notes)
        }

    }

}