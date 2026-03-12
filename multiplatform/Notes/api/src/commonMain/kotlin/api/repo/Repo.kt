package api.repo

import api.Platform
import api.data.Attachments
import api.data.Notes
import api.data.UserFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow

interface RepoCallback {
    fun onNoteAdded(id: Long)
    fun onEditorNavBack()
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
        onDeleted: (Long) -> Unit,
    )

    suspend fun onPasswordChanged()

    suspend fun canChangePassword(): Boolean

    suspend fun clearLocalAppStorage()

    suspend fun isDataInSync(): Boolean

    fun clear()

    suspend fun onAttachments(
        attachment: Any,
        noteId: Long,
        info: Any? = null,
    ): Boolean = false

    fun getAttachments(): Flow<Attachments>

    suspend fun onDeleteAttachment(file: UserFile): Boolean = false
}

abstract class BaseRepo : Repository {
    private val coroutineContext = SupervisorJob() + Dispatchers.IO
    protected val scope = CoroutineScope(coroutineContext)

    override fun clear() {
        Platform().logger.logi("BaseRepo::clear()")
        scope.cancel()
    }
}
