package api.repo

import api.data.Attachments
import api.data.Image
import api.data.Notes
import kotlinx.coroutines.flow.Flow
import java.io.File

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

    fun onAttachments(attachment: Any, noteId: Long, info: Any? = null) {}

    fun getAttachments(filesDir: File) : Flow<Attachments>

    fun onDelete(image: Image) {}
}

abstract class BaseRepo(protected val rootFileDir: File) : Repository
