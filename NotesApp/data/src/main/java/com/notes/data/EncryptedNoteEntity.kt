package com.notes.data

import com.notes.api.PlatformAPIs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EncryptedNoteEntity(private val original: NoteDao) : NoteDao {

    override fun getNotes(): Flow<List<NoteEntity>> {
        return original.getNotes().map { list ->
            // Create new list with decrypted notes
            list.map { note -> decrypt(note) }
        }
    }

    override fun getNote(id: Long): Flow<NoteEntity?> {
        return original.getNote(id).map { note ->
            if (note == null) null else decrypt(note)
        }
    }

    override suspend fun insertNote(note: NoteEntity): Long {
        val encrypted = encrypt(note)
        return original.insertNote(encrypted)
    }

    override suspend fun deleteNote(note: NoteEntity) {
        // No need to encrypt
        return original.deleteNote(note)
    }

    override suspend fun updateNote(note: NoteEntity) {
        val encrypted = encrypt(note)
        return original.updateNote(encrypted)
    }

    private fun decrypt(noteEntity: NoteEntity): NoteEntity {
        val plaintext = PlatformAPIs.crypto.decrypt(noteEntity.content)
        return noteEntity.copy(content = plaintext)
    }

    private fun encrypt(noteEntity: NoteEntity): NoteEntity {
        val ciphertext = PlatformAPIs.crypto.encrypt(noteEntity.content)
        return noteEntity.copy(content = ciphertext)
    }
}