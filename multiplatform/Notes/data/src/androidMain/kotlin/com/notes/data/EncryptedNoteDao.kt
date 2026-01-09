package com.notes.data

import api.PlatformAPIs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EncryptedNoteDao(
    private val original: NoteDao,
) : NoteDao by original {
    override fun getNotes(): Flow<List<NoteEntity>> =
        original.getNotes().map { list ->
            // Create new list with decrypted notes
            list.map { note -> decrypt(note) }
        }

    override fun getNote(id: Long): Flow<NoteEntity?> =
        original.getNote(id).map { note ->
            note?.let { decrypt(it) }
        }

    override suspend fun insertNote(note: NoteEntity): Long {
        val encrypted = encrypt(note)
        return original.insertNote(encrypted)
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
