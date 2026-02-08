package com.notes.data

import api.PlatformAPIs.logger
import api.data.Notes
import com.notes.data.json.isPendingDeletionOnRemote
import com.notes.data.json.isPendingUpdateOnRemote
import com.notes.data.json.update
import kotlinx.coroutines.flow.first

/**
 * Helper function for convenience
 */

suspend fun updateMetadataForNote(
    note: Notes,
    pendingDelete: Boolean? = null,
    pendingUpdate: Boolean? = null
) {
    val db = LocalNoteDatabase.access()
    val noteInfo = db.getNoteWithMetadata(note.id).first()
    if (noteInfo != null && noteInfo.metadataId != null) {
        val metaDb = LocalNoteDatabase.accessNoteMetadata()
        val currEntity = metaDb.getMetadata(noteInfo.metadataId).first()
        val newMetadata = currEntity!!.update(pendingDelete = pendingDelete, pendingUpdate = pendingUpdate)
        metaDb.updateMetadata(currEntity.copy(metadata = newMetadata, pendingDelete = true))
        logger.logi("updateMetadataForNote() updated for = ${note.id}")
    }
}

suspend fun isAllInSyncWithRemote(): Boolean {
    val db = LocalNoteDatabase.accessNoteMetadata()
    val metadataList = db.getAllMetadata().first()
    for (metadata in metadataList) {
        if (metadata.isPendingDeletionOnRemote() || metadata.isPendingUpdateOnRemote()) {
            logger.loge("isAllInSyncWithRemote() not in sync, " +
                    "pending delete = ${metadata.isPendingDeletionOnRemote()}, " +
                    "pending update = ${metadata.isPendingUpdateOnRemote()}\n" +
                    "meta data = $metadata")
            return false
        }
    }
    return true
}