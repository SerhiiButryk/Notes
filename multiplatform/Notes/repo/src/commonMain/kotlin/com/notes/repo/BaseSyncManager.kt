package com.notes.repo

import api.Platform
import api.data.AbstractStorageService
import api.data.Notes
import com.notes.db.AppDatabase
import com.notes.db.ClientSyncManager
import com.notes.db.OnAction
import com.notes.db.impl.isPendingDeletionOnRemote
import com.notes.db.impl.isPendingUpdateOnRemote
import com.notes.db.impl.updateForDatastore
import com.notes.db.model.NoteMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

abstract class BaseSyncManager(
    private val database: AppDatabase
) : ClientSyncManager {

    private val tag = "BaseSyncManager"

    override var scope: CoroutineScope? = null

    protected val fileManager = FilesManager()

    private val metadataMutex = Mutex()

    override fun sync(
        scope: CoroutineScope,
        action: OnAction
    ) {
        Platform().logger.logi("$tag:syncIfNeeded()")
        scope.launch {
            val metadata = database.fetch()
            for (item in metadata) {

                val noteId = item.noteId!!

                val exists = fileManager.hasNoteById(id = noteId, path = fileManager.secondCacheDir)

                if (exists) {

                    if (item.metadata.isPendingUpdateOnRemote()) {
                        val note = Notes(id = noteId)
                        action.onSaveRequired(note)
                    }

                    if (item.metadata.isPendingDeletionOnRemote()) {
                        val note = Notes(id = noteId)
                        action.onDeleteRequired(note)
                    }

                } else {
                    Platform().logger.logi("$tag:syncIfNeeded() might be in a wrong state, " +
                            "no file for $noteId")
                }

            }
        }
    }

    override suspend fun markPendingDeletion(note: Notes) {
        val foundRecord = searchMetadataFor(note.id)
        if (foundRecord == null) {
            database.insert(
                NoteMetadata(
                    pendingDelete = true,
                    noteId = note.id,
                )
            )
            Platform().logger.logi("$tag:markDeleteLocally() new added for ${note.id}")
        } else {
            database.update(foundRecord.copy(pendingDelete = true))
            Platform().logger.logi("$tag:markDeleteLocally() for ${note.id}")
        }
    }

    override suspend fun delete(note: Notes) {
        metadataMutex.withLock {
            val foundRecord = searchMetadataFor(note.id)
            if (foundRecord == null) {
                Platform().logger.loge("$tag:deleteLocally() no record for ${note.id}")
                return
            } else {
                val metadata = foundRecord.metadata
                if (!metadata.isPendingDeletionOnRemote() && foundRecord.pendingDelete) {
                    database.delete(foundRecord.id)
                    val filePath = fileManager.secondCacheDir + "/" + note.id
                    fileManager.delete(File(filePath))
                    Platform().logger.logi("$tag:deleteLocally() for ${note.id} done")
                }
            }
        }
    }

    override suspend fun updateMetadata(
        dataStore: AbstractStorageService,
        note: Notes,
        pendingUpdate: Boolean?,
        pendingDelete: Boolean?
    ) {
        metadataMutex.withLock {

            val foundMetadata = searchMetadataFor(note.id)

            if (foundMetadata != null) {

                val newMetadata = foundMetadata.metadata.updateForDatastore(
                    dataStore = dataStore,
                    pendingDelete = pendingDelete,
                    pendingUpdate = pendingUpdate,
                )

                database.update(foundMetadata.copy(metadata = newMetadata))

                val foundMetadata = searchMetadataFor(note.id)

                Platform().logger.logi(
                    "$tag:updateLocally() updated metadata id = '${foundMetadata?.id}', " +
                            "note id = '${foundMetadata?.noteId}' for '${dataStore.key}', " +
                            "metadata = ${foundMetadata?.metadata}",
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
    }

    override suspend fun store(
        notes: List<Notes>,
        forceOverride: Boolean,
        scope: CoroutineScope
    ) {
        fileManager.writeCache(notes = notes, cacheDir = fileManager.secondCacheDir)
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

    override suspend fun clearLocalStorage() {
        database.deleteAll()
        fileManager.clearCache()
        Platform().logger.logi("$tag:clearLocalStorage(): Local storage has been cleared")
    }

    suspend fun __getMetadata_FOR_TEST() = database.fetch()

    suspend fun __getDatabase_FOR_TEST() = database

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


}