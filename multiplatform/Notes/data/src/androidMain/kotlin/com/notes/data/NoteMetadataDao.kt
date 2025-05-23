package com.notes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteMetadataDao {

    @Query("select * from notes_metadata")
    fun getAllMetadata(): Flow<List<NotesMetadataEntity>>

    @Query("select * from notes_metadata WHERE uid = :id")
    fun getMetadata(id: Long): Flow<NotesMetadataEntity?>

    @Insert
    suspend fun insertMetadata(note: NotesMetadataEntity): Long

    @Delete
    suspend fun deleteMetadata(note: NotesMetadataEntity)

    @Update
    suspend fun updateMetadata(note: NotesMetadataEntity)

}