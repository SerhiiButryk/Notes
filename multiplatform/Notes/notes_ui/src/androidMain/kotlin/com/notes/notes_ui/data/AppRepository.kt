package com.notes.notes_ui.data

import android.content.Context
import android.os.Build
import com.notes.api.PlatformAPIs.logger
import com.notes.api.data.Notes
import com.notes.data.LocalNoteDatabase
import com.notes.data.NoteEntity
import com.notes.notes_ui.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AppRepository : Repository {
    private val coroutineContext = SupervisorJob() + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)
    private val remoteRepository = RemoteRepository()

    override fun getNotes(): Flow<List<Notes>> =
        flow {
            logger.logi("OfflineRepository::getNotes()")

            // TODO: Might do this periodically
            // in Work manager or something else

            // Trigger fetch from remote server
            remoteRepository.fetchNotes(scope)

            val db = LocalNoteDatabase.access()

            // Trigger load from local db
            db
                .getNotes()
                .map { list ->
                    list.map { it.toNote() }
                }.collect {
                    emit(it)
                }
        }.flowOn(Dispatchers.IO)

    override fun getNotes(id: Long): Flow<Notes?> =
        flow {
            logger.logi("OfflineRepository::getNotes(id=$id)")

            val db = LocalNoteDatabase.access()

            db
                .getNote(id)
                .map { it?.toNote() }
                .collect {
                    emit(it)
                }
        }.flowOn(Dispatchers.IO)

    override fun saveNote(
        note: Notes,
        onNewAdded: suspend (Long) -> Unit,
    ) = saveNote(scope = scope, note = note, onNewAdded = onNewAdded)

    fun saveNote(
        scope: CoroutineScope,
        note: Notes,
        onNewAdded: suspend (Long) -> Unit,
    ) {
        scope.launch {
            val db = LocalNoteDatabase.access()
            val newNote = db.getNote(note.id).first() == null
            if (newNote) {
                val id = db.insertNote(note.toEntity(setId = false /* Use auto-increment */))
                onNewAdded(id)
                logger.logi("OfflineRepository::saveNote($id) new record is added locally")
                remoteRepository.saveNote(scope, note.copy(id = id))
            } else {
                db.updateNote(note.toEntity())
                logger.logi("OfflineRepository::saveNote(${note.id}) is updated locally")
                remoteRepository.saveNote(scope, note)
            }
        }
    }

    override fun deleteNote(
        note: Notes,
        callback: (Long) -> Unit,
    ) = deleteNote(scope = scope, note = note, callback = callback)

    // TODO: Double-check delete behavior
    // What happens with the table ? Is private ket get reused ?
    fun deleteNote(
        scope: CoroutineScope,
        note: Notes,
        callback: (Long) -> Unit,
    ) {
        scope.launch {
            logger.logi("OfflineRepository::deleteNote(${note.id})")
            // Deliberately set default value, only uid is important
            // as it is used as primary key to match the note
            val noteEntity = NoteEntity(uid = note.id, "", "", "", true)
            LocalNoteDatabase.access().deleteNote(noteEntity)
            callback(note.id)
            remoteRepository.delete(scope, note)
        }
    }

    override fun init(context: Context) {
        scope.launch {
            logger.logi("OfflineRepository::init()")
            LocalNoteDatabase.initialize(context)
        }
    }

    override fun clear() {
        logger.logi("OfflineRepository::clear()")
        scope.cancel()
        LocalNoteDatabase.close()
    }
}

fun Notes.toEntity(setId: Boolean = true): NoteEntity {
    // TODO: Add test
    val time: String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            LocalDateTime.now().format(formatter)
        } else {
            Calendar.getInstance().getTime().toString()
        }
    return if (setId) {
        NoteEntity(
            uid = id,
            userId = userId,
            content = content,
            time = time,
            pendingUpdate = pendingUpdate,
        )
    } else {
        NoteEntity(userId = userId, content = content, time = time, pendingUpdate = pendingUpdate)
    }
}

fun NoteEntity.toNote(): Notes = Notes(content = content, id = uid, userId = userId, time = time)
