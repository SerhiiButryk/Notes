package api.repo

import api.data.Notes
import kotlinx.coroutines.flow.Flow

interface RepoCallback {
    fun onNoteAdded(id: Long)
    fun onNoteDeleted(id: Long)
    fun onNoteNavigateBack()
}

interface Repository {
    fun getNotes(): Flow<List<Notes>>

    fun getNotes(id: Long): Flow<Notes?>

    fun saveNote(
        note: Notes,
        onNewAdded: suspend (Long) -> Unit,
    )

    fun deleteNote(
        note: Notes,
        callback: (Long) -> Unit,
    )

    suspend fun triggerSyncWithRemote()

    suspend fun clearLocalNotesStorage()

    suspend fun isDataInSync(): Boolean

    fun clear()
}
