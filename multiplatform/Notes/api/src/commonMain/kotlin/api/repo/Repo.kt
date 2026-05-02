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

    suspend fun onPasswordChanged()

    suspend fun canChangePassword(): Boolean

    suspend fun clearLocalAppStorage()

    suspend fun isDataInSync(): Boolean

    fun clear()
}
