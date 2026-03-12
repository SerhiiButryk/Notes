package com.notes.db.impl

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteMetadataDao {

    @Query("select * from notes_metadata")
    fun getAllMetadata(): Flow<List<MetadataEntity>>

    @Query("select * from notes_metadata WHERE uid = :id")
    fun getMetadata(id: Long): Flow<MetadataEntity?>

    @Insert
    suspend fun insertMetadata(note: MetadataEntity): Long

    @Delete
    suspend fun deleteMetadata(note: MetadataEntity)

    @Update
    suspend fun updateMetadata(note: MetadataEntity)
}
