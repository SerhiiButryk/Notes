package com.notes.db

import api.Platform
import com.notes.db.json.isPendingDeletionOnRemote
import com.notes.db.json.isPendingUpdateOnRemote
import kotlinx.coroutines.flow.first

suspend fun isAllInSyncWithRemote(): Boolean {
    val db = LocalNoteDatabase.accessNoteMetadata()
    val metadataList = db.getAllMetadata().first()
    for (metadata in metadataList) {
        if (metadata.isPendingDeletionOnRemote() || metadata.isPendingUpdateOnRemote()) {
            Platform().logger.loge("isAllInSyncWithRemote() not in sync, " +
                    "pending delete = ${metadata.isPendingDeletionOnRemote()}, " +
                    "pending update = ${metadata.isPendingUpdateOnRemote()}\n" +
                    "meta data = $metadata")
            return false
        }
    }
    return true
}