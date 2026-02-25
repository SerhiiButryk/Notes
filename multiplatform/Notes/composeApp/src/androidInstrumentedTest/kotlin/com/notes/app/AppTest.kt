package com.notes.app

import com.google.common.truth.Truth.assertThat
import com.notes.data.LocalNoteDatabase
import kotlinx.coroutines.flow.first

open class AppTest {

    protected suspend fun preConditionCheck() {

        assertThat(LocalNoteDatabase.initialize()).isNotNull()
        assertThat(LocalNoteDatabase.access()).isNotNull()
        assertThat(LocalNoteDatabase.accessNoteMetadata()).isNotNull()

        assertThat(LocalNoteDatabase.access()
            .getNotes().first().isEmpty()).isTrue()

        assertThat(LocalNoteDatabase.accessNoteMetadata().
        getAllMetadata().first().isEmpty()).isTrue()
    }

    protected suspend fun postConditionCheck() {

        val listMetaData = LocalNoteDatabase.accessNoteMetadata()
            .getAllMetadata().first()

        for (item in listMetaData) {
            LocalNoteDatabase.accessNoteMetadata().deleteMetadata(item)
        }

        val listNotes = LocalNoteDatabase.access()
            .getNotes().first()

        for (item in listNotes) {
            LocalNoteDatabase.access().deleteNote(item)
        }

        assertThat(LocalNoteDatabase.access()
            .getNotes().first().isEmpty()).isTrue()

        assertThat(LocalNoteDatabase.accessNoteMetadata()
            .getAllMetadata().first().isEmpty()).isTrue()

    }

}