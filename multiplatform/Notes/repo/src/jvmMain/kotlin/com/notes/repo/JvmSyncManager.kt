package com.notes.repo

import api.Platform
import api.data.AbstractStorageService
import api.data.Notes
import com.notes.db.ClientSyncManager
import com.notes.db.OnAction
import com.notes.db.impl.getDatabaseInstance
import com.notes.db.impl.isPendingDeletionOnRemote
import com.notes.db.impl.isPendingUpdateOnRemote
import com.notes.db.impl.updateForDatastore
import com.notes.db.model.NoteMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchKey
import kotlin.concurrent.thread

class JvmSyncManager : ClientSyncManager {

    private val fileManager = FilesManager()
    val cacheDir = Platform().getCacheDir() + "/cache"
    private val tag = "JvmSyncManager"

    private val scanSignal = Channel<Unit>()

    val database = getDatabaseInstance()

    init {
        startFileObserver(cacheDir)
    }

    override val notes: Flow<List<Notes>> = flow {
        // Get initial data
        val notes = fileManager.readCache(cacheDir)
        emit(notes)
        while (true) {
            scanSignal.receive()
            Platform().logger.logi("$tag: received file change event")
            // Rescan folder
            val notes = fileManager.readCache(cacheDir)
            Platform().logger.logi("$tag: emitting...")
            emit(notes)
        }
    }

    override fun sync(
        scope: CoroutineScope,
        action: OnAction
    ) {
        Platform().logger.logi("$tag:syncIfNeeded()")
        scope.launch {
            val metadata = database.fetch()
            for (item in metadata) {

                val noteId = item.noteId!!

                val exists = fileManager.hasNoteById(id = noteId, path = cacheDir)

                if (exists) {

                    val note = Notes(id = noteId)

                    if (item.metadata.isPendingUpdateOnRemote()) {
//                        action.onSaveRequired(note)
                    }

                    if (item.metadata.isPendingDeletionOnRemote()) {
//                        action.onDeleteRequired(note)
                    }

                } else {
                    Platform().logger.logi("$tag:syncIfNeeded() may be in a wrong state, " +
                            "no file for $noteId")
                }

            }
        }
    }

    override suspend fun markPendingDeletion(note: Notes) {
        val foundRecord = searchMetadataFor(note.id)
        if (foundRecord == null) {
            database.insert(NoteMetadata(
                pendingDelete = true,
                noteId = note.id,
            ))
            Platform().logger.logi("$tag:markDeleteLocally() new added for ${note.id}")
        } else {
            database.update(foundRecord.copy(pendingDelete = true))
            Platform().logger.logi("$tag:markDeleteLocally() for ${note.id}")
        }
    }

    override suspend fun delete(note: Notes) {

        val foundRecord = searchMetadataFor(note.id)

        if (foundRecord == null) {
            Platform().logger.loge("$tag:deleteLocally() no record for ${note.id}")
            return
        } else {
            val metadata = foundRecord.metadata
            if (!metadata.isPendingDeletionOnRemote() && foundRecord.pendingDelete) {
                database.delete(foundRecord.id)
                val filePath = cacheDir + "/" + note.id
                fileManager.delete(File(filePath))
                Platform().logger.logi("$tag:deleteLocally() for ${note.id} done")
            }
        }
    }

    override suspend fun updateMetadata(
        dataStore: AbstractStorageService,
        note: Notes,
        pendingUpdate: Boolean?,
        pendingDelete: Boolean?
    ) {

        val foundMetadata = searchMetadataFor(note.id)

        if (foundMetadata != null) {

            val newMetadata = foundMetadata.metadata.updateForDatastore(
                dataStore = dataStore,
                pendingDelete = pendingDelete,
                pendingUpdate = pendingUpdate,
            )

            database.update(foundMetadata.copy(metadata = newMetadata))

            Platform().logger.logi(
                "$tag:updateLocally() updated metadata id = '${foundMetadata.id}', " +
                        "note id = '${foundMetadata.noteId}' for '${dataStore.key}'",
            )

        } else {

            val metadata = NoteMetadata(noteId = note.id)

            val updated =
                metadata.metadata.updateForDatastore(
                    dataStore = dataStore,
                    pendingDelete = pendingDelete,
                    pendingUpdate = pendingUpdate,
                )

            database.insert(metadata.copy(metadata = updated))

            Platform().logger.logi(
                "$tag:updateLocally() created metadata id = '${metadata.id}', " +
                        "note id = '${metadata.noteId}' for '${dataStore.key}'",
            )
        }

    }

    override suspend fun store(
        notes: List<Notes>,
        forceOverride: Boolean,
        scope: CoroutineScope
    ) {
        fileManager.cacheNotes(notes = notes, cacheDir = cacheDir)
    }

    override suspend fun isAllInSync(): Boolean {
        val metadataList = database.fetch()
        var isInSync = true
        for (metadata in metadataList) {
            if (metadata.metadata.isPendingDeletionOnRemote() ||
                metadata.metadata.isPendingUpdateOnRemote()) {
                // Log all records which are not updated
                Platform().logger.logi(
                    "$tag:isAllInSyncWithRemote() not in sync, " +
                            "pending delete = '${metadata.metadata.isPendingDeletionOnRemote()}', " +
                            "pending update = '${metadata.metadata.isPendingUpdateOnRemote()}'\n" +
                            "meta data = '$metadata'",
                )
                if (isInSync) {
                    isInSync = false
                }
            }
        }
        return isInSync
    }

    private suspend fun searchMetadataFor(noteId: Long): NoteMetadata? {
        val records = database.fetch()
        for (metadata in records) {
            if (metadata.noteId == noteId) {
                Platform().logger.logi("$tag:searchMetadataFor() found record for '$noteId'")
                return metadata
            }
        }
        Platform().logger.loge("$tag:searchMetadataFor() no record for '$noteId'")
        return null
    }

    private fun startFileObserver(dirPath: String) {

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