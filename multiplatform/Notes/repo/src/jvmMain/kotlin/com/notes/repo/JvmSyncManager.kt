package com.notes.repo

import api.Platform
import api.data.Notes
import com.notes.db.impl.getDatabaseInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchKey
import kotlin.concurrent.thread

class JvmSyncManager : BaseSyncManager(getDatabaseInstance()) {

    private val tag = "JvmSyncManager"

    private val scanSignal = Channel<Unit>()

    init {
        startCacheDirWatching()
    }

    override val notes: Flow<List<Notes>> = flow {
        // Get initial data
        val notes = fileManager.readCache(fileManager.secondCacheDir)
        emit(notes)
        while (true) {
            scanSignal.receive()
            Platform().logger.logi("$tag: received file change event")
            // Rescan folder
            val notes = fileManager.readCache(fileManager.secondCacheDir)
            Platform().logger.logi("$tag: emitting...")
            emit(notes)
        }
    }


    override fun startCacheDirWatching(scope: CoroutineScope?) {

        val dirPath = fileManager.secondCacheDir

        File(dirPath).apply { mkdirs() }

        val watchService = FileSystems.getDefault().newWatchService()

        // Register events you want to listen to
        Path.of(dirPath).register(
            watchService,
            ENTRY_CREATE,
            ENTRY_DELETE,
            ENTRY_MODIFY
        )

        // Run the event loop on a background thread
        thread(isDaemon = true) {
            Platform().logger.logd("$tag: Starting watching '$dirPath'")
            try {
                while (true) {
                    val key: WatchKey = watchService.take() // Blocks until an event occurs

                    for (event in key.pollEvents()) {
                        val kind = event.kind()
                        val filename = event.context() as Path

                        Platform().logger.logd("$tag: Event $kind on file: $filename")

                        scanSignal.trySend(Unit)
                    }

                    // Reset the key to continue receiving events; exit loop if dir is unreachable
                    val valid = key.reset()
                    if (!valid) break
                }
            } catch (e: Exception) {
                Platform().logger.loge("$tag: error during file watching: $e")
            } finally {
                Platform().logger.logi("$tag: End watching '$dirPath'")
                watchService.close()
            }
        }
    }

}