package com.notes.db

import api.data.AbstractStorageService
import api.data.Notes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface OnAction {
    fun onDeleteRequired(note: Notes)
    fun onSaveRequired(note: Notes)
}

interface ClientSyncManager {

    val notes: Flow<List<Notes>>

    fun sync(scope: CoroutineScope, action: OnAction)

    suspend fun markPendingDeletion(note: Notes)

    suspend fun delete(note: Notes)

    suspend fun updateMetadata(
        dataStore: AbstractStorageService,
        note: Notes,
        pendingUpdate: Boolean? = null,
        pendingDelete: Boolean? = null,
    )

    suspend fun store(
        notes: List<Notes>,
        forceOverride: Boolean,
        scope: CoroutineScope,
    )

    suspend fun isAllInSync() = false

    suspend fun clearLocalStorage() {}

}