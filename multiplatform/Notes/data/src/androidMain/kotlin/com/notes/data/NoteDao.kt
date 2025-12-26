package com.notes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // Functions which return Flow are not suspended
    // This is not a strange thing. As flow defines some operation
    // but they are triggered and controlled by the caller. So it's nonsense
    // to make them suspended

    @Query("select * from notes_data")
    fun getNotes(): Flow<List<NoteEntity>>

    @Query("select * from notes_data WHERE uid = :id")
    fun getNote(id: Long): Flow<NoteEntity?>

    @Insert
    suspend fun insertNote(note: NoteEntity): Long

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

}