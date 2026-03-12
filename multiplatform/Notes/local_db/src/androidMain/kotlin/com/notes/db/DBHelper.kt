package com.notes.db

import api.Platform
import com.notes.db.impl.isPendingDeletionOnRemote
import com.notes.db.impl.isPendingUpdateOnRemote
import kotlinx.coroutines.flow.first

suspend fun isAllInSyncWithRemote(): Boolean {
    val db = LocalNoteDatabase.accessNoteMetadata()
    val metadataList = db.getAllMetadata().first()
    var isInSync = true
    for (metadata in metadataList) {
        if (metadata.metadata.isPendingDeletionOnRemote() ||
            metadata.metadata.isPendingUpdateOnRemote()) {
            // Log for debugging
            Platform().logger.loge(
                "isAllInSyncWithRemote() not in sync, " +
                    "pending delete = ${metadata.metadata.isPendingDeletionOnRemote()}, " +
                    "pending update = ${metadata.metadata.isPendingUpdateOnRemote()}\n" +
                    "meta data = $metadata",
            )
            if (isInSync) {
                isInSync = false
            }
        }
    }
    return isInSync
}
