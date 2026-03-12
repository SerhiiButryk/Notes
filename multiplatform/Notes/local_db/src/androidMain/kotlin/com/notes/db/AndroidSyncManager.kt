package com.notes.db

import api.Platform
import api.data.AbstractStorageService
import api.data.Notes
import com.notes.db.impl.isPendingDeletionOnRemote
import com.notes.db.impl.isPendingUpdateOnRemote
import com.notes.db.impl.update
import com.notes.db.impl.updateForDatastore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AndroidSyncManager : ClientSyncManager {

    override val notes: Flow<List<Notes>> = emptyFlow()

    private val protectMetadataUpdate = Mutex()

    override fun sync(
        scope: CoroutineScope,
        action: OnAction
    ) {
        Platform().logger.logi("AndroidSyncManager::sync()")
        scope.launch {
            val metaDb = LocalNoteDatabase.accessNoteMetadata()
            val currMetadata = metaDb.getAllMetadata().first()

            for (metadata in currMetadata) {
                launch {
                    val db = LocalNoteDatabase.access()
                    val note = db.getNote(metadata.original!!).first()

                    if (note != null) {
                        val noteLocal = note.toNote()

                        if (metadata.metadata.isPendingUpdateOnRemote()) {
                            action.onSaveRequired(noteLocal)
                        }

                        if (metadata.metadata.isPendingDeletionOnRemote()) {
                            action.onDeleteRequired(noteLocal)
                        }
                    }
                }
            }
        }
    }

    override suspend fun markPendingDeletion(note: Notes) {
        val db = LocalNoteDatabase.access()
        val metaDb = LocalNoteDatabase.accessNoteMetadata()

        val noteInfo = db.getNoteWithMetadata(note.id).first()

        if (noteInfo != null && noteInfo.metadataId != null) {
            val currMetadata = metaDb.getMetadata(noteInfo.metadataId).first()
            if (currMetadata != null) {
                metaDb.updateMetadata(currMetadata.copy(pendingDelete = true))
                Platform().logger.logi("AndroidSyncManager::markDeleteLocally() set for ${note.id}")
            }
        }
    }

    override suspend fun delete(note: Notes) {
        val db = LocalNoteDatabase.access()
        val metaDb = LocalNoteDatabase.accessNoteMetadata()

        val noteInfo = db.getNoteWithMetadata(note.id).first()

        if (noteInfo != null && noteInfo.metadataId != null) {
            val metadata = metaDb.getMetadata(noteInfo.metadataId).first()

            // Can delete locally
            if (metadata != null && !metadata.metadata.isPendingDeletionOnRemote() && metadata.pendingDelete) {
                // Deliberately set default value, only uid is important
                // as it is used to select the record
                val noteEntity = NoteEntity(uid = note.id)
                LocalNoteDatabase.access().deleteNote(noteEntity)
                Platform().logger.logi("AndroidSyncManager::deleteNoteLocally() deleted locally = ${note.id}")
            }
        }
    }

    override suspend fun updateMetadata(
        dataStore: AbstractStorageService,
        note: Notes,
        pendingUpdate: Boolean?,
        pendingDelete: Boolean?
    ) {
        protectMetadataUpdate.withLock {
            val db = LocalNoteDatabase.access()
            val metaDb = LocalNoteDatabase.accessNoteMetadata()

            val noteInfo = db.getNoteWithMetadata(note.id).first()

            if (noteInfo != null && noteInfo.metadataId != null) {
                // Update current

                val currMetadata = metaDb.getMetadata(noteInfo.metadataId).first()

                if (currMetadata != null) {
                    val newMetadata =
                        currMetadata.metadata.updateForDatastore(
                            dataStore = dataStore,
                            pendingDelete = pendingDelete,
                            pendingUpdate = pendingUpdate,
                        )

                    metaDb.updateMetadata(currMetadata.copy(metadata = newMetadata))

                    Platform().logger.logi(
                        "AndroidSyncManager::updateMetadata() updated meta id = '${currMetadata.uid}', " +
                                "note id = '${currMetadata.original}' for '${dataStore.key}'",
                    )
                } else {
                    Platform().logger.loge(
                        "AndroidSyncManager::updateMetadata() absent for '${dataStore.key}'",
                    )
                }
            } else {
                // If there are no metadata then create it

                val newMetadata =
                    NotesMetadataEntity(
                        original = note.id,
                        metadata = "",
                    )

                val updated =
                    newMetadata.metadata.updateForDatastore(
                        dataStore = dataStore,
                        pendingDelete = pendingDelete,
                        pendingUpdate = pendingUpdate,
                    )

                val id = metaDb.insertMetadata(newMetadata.copy(metadata = updated))

                Platform().logger.logi(
                    "AndroidSyncManager::updateMetadata() added new metadata = " +
                            "'$id' for ${dataStore.key}",
                )
            }
        }
    }

    override suspend fun store(
        notes: List<Notes>,
        forceOverride: Boolean,
        scope: CoroutineScope
    ) {
        Platform().logger.logi("AndroidSyncManager::storeLocally()")

        for (note in notes) {
            scope.launch {
                val db = LocalNoteDatabase.access()
                val metadataDb = LocalNoteDatabase.accessNoteMetadata()

                val curr = db.getNote(note.id).first()
                if (curr == null) {
                    // Store note and its metadata records

                    val id = db.insertNote(note.toEntity())

                    val metadata = NotesMetadataEntity(original = id, metadata = "")

                    val newMetadata =
                        metadata.metadata.update(
                            pendingUpdateFirebase = false,
                            pendingUpdateGoogle = false,
                            pendingDeleteGoogle = false,
                            pendingDeleteFirebase = false,
                        )

                    val metaId = metadataDb.insertMetadata(metadata.copy(metadata = newMetadata))

                    Platform().logger.logi("AndroidSyncManager::storeLocally() added note = $id, meta id = $metaId")
                } else if (forceOverride) {
                    // Assuming we don't need to change metadata
                    db.updateNote(note.toEntity(setId = true))
                    Platform().logger.logi("AndroidSyncManager::storeLocally() overridden note = ${curr.uid}")
                } else {
                    Platform().logger.logi("AndroidSyncManager::storeLocally() no-op, already have note = ${curr.uid}")
                }
            }
        }
    }
}