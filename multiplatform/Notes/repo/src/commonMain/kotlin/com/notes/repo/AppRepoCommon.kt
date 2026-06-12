package com.notes.repo

import api.data.Attachments
import api.data.Image
import api.data.Notes
import api.repo.BaseRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class AppRepoCommon : BaseRepo() {

    override fun getNotes(): Flow<List<Notes>> {
        return emptyFlow()
    }

    override fun getNotes(id: Long): Flow<Notes?> {
        return emptyFlow()
    }

    override fun saveNote(note: Notes, onNewAdded: suspend (Long) -> Unit) {
    }

    override fun deleteNote(note: Notes, onDeleted: (Long) -> Unit) {
    }

    override suspend fun onPasswordChanged() {
    }

    override suspend fun canChangePassword(): Boolean {
        return false
    }

    override suspend fun clearLocalAppStorage() {
    }

    override suspend fun isDataInSync(): Boolean {
        return false
    }

    override fun clear() {

    }

    override fun onAttachments(attachment: Any, noteId: Long, info: Any?) {
    }

    override fun getAttachments(): Flow<Attachments> {
        return emptyFlow()
    }

    override fun onDelete(image: Image) {
    }

}