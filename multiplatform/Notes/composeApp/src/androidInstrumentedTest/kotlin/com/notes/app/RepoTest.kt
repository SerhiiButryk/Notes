package com.notes.app

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import api.data.AbstractStorageService
import com.google.common.truth.Truth.assertThat
import api.data.Document
import api.data.Notes
import com.notes.data.LocalNoteDatabase
import com.notes.notes_ui.data.AppRepository
import com.notes.notes_ui.data.RemoteRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@RunWith(AndroidJUnit4::class)
class RepoTest : AppTest() {

    private val appContext: Context = InstrumentationRegistry
        .getInstrumentation().targetContext.applicationContext

    private val tag = "RepoTest"

    private var mockedStoreServiceStoreResult = true
    private var mockedStoreServiceDeleteResult = true

    private val mockedStoreServiceFirebase = object : AbstractStorageService() {

        override val name: String = "firebase"

        override suspend fun store(document: Document): Boolean {
            return mockedStoreServiceStoreResult
        }

        override suspend fun load(name: String): Document {
            return Document("", "")
        }

        override suspend fun delete(name: String): Boolean {
            return mockedStoreServiceDeleteResult
        }

        override suspend fun fetchAll(): List<Document> {
            return emptyList()
        }

    }

    private val mockedStoreServiceGoogle = object : AbstractStorageService() {

        override val name: String = "googledrive"

        override suspend fun store(document: Document): Boolean {
            return mockedStoreServiceStoreResult
        }

        override suspend fun load(name: String): Document {
            return Document("", "")
        }

        override suspend fun delete(name: String): Boolean {
            return mockedStoreServiceDeleteResult
        }

        override suspend fun fetchAll(): List<Document> {
            return emptyList()
        }

    }

    private val remoteRepo = RemoteRepository(
        listOf(mockedStoreServiceGoogle, mockedStoreServiceFirebase)
    )

    @Before
    fun onStart() {
        LocalNoteDatabase.initialize(appContext)
    }

    @After
    fun onFinish() {
        LocalNoteDatabase.close()
    }

    @Test
    fun test01_insert_new_note() = runTest {

        preConditionCheck()

        val repo = AppRepository(remoteRepository = remoteRepo)

        // Check that no data
        verifyDBIsEmpty(repo)

        val note = Notes(content = "some content", userId = "userId", time = "time")

        val callbackCalled = AtomicBoolean(false)
        var id = -1L

        // Will not return unless the task is completed
        coroutineScope {
            repo.saveNote(this, note, onNewAdded = {
                Log.i(tag, "test01_insert_new_note: callback is called id = $it")
                // Should be called when a record is added
                callbackCalled.store(true)
                id = it
            })
        }

        // Check that 1 note
        val noteListAfter = repo.getNotes().first()

        assertThat(noteListAfter.isEmpty()).isFalse()
        assertThat(noteListAfter.size).isEqualTo(1)

        assertThat(callbackCalled.load()).isTrue()
        assertThat(noteListAfter[0].id).isEqualTo(id)

        // Will not return unless the task is completed
        coroutineScope {
            repo.deleteNote(scope = this, Notes(id = id), {})
        }

        // Check that no data
        verifyDBIsEmpty(repo)

        repo.clear()

        postConditionCheck()

        Log.i(tag, "test01_insert_new_note: done")
    }

    @Test
    fun test02_update_existed_note() = runTest {

        preConditionCheck()

        val repo = AppRepository(remoteRepository = remoteRepo)

        // Check that no data
        verifyDBIsEmpty(repo)

        val note = Notes(content = "some content", userId = "userId", time = "time")

        var id = -1L

        // Will not return unless the task is completed
        coroutineScope {
            repo.saveNote(scope = this, note) {
                Log.i(tag, "test02_update_existed_note: callback is called")
                // Should be called when a record is added
                id = it
            }
        }

        // Check that 1 note
        val noteListAfter = repo.getNotes().first()

        assertThat(noteListAfter.isEmpty()).isFalse()
        assertThat(noteListAfter.size).isEqualTo(1)

        assertThat(noteListAfter[0].id).isEqualTo(id)

        val callbackCalled = AtomicBoolean(false)

        // Will not return unless the task is completed
        coroutineScope {
            repo.saveNote(scope = this, note.copy(id = id, content = "new content")) {
                callbackCalled.store(true)
            }
        }

        assertThat(callbackCalled.load()).isFalse()

        // Check that still one note not more
        val noteListAfterUpdate = repo.getNotes().first()

        assertThat(noteListAfterUpdate.isEmpty()).isFalse()
        assertThat(noteListAfterUpdate.size).isEqualTo(1)

        val updatedNote = repo.getNotes(id = id).first()!!

        with(updatedNote) {
            assertThat(this.content).isEqualTo("new content")
            assertThat(this.id).isEqualTo(id)
            assertThat(this.userId).isEqualTo("userId")
        }

        // Will not return unless the task is completed
        coroutineScope {
            repo.deleteNote(scope = this, Notes(id = id), {})
        }

        // Check that no data
        verifyDBIsEmpty(repo)

        repo.clear()

        postConditionCheck()

        Log.i(tag, "test02_update_existed_note: done")
    }

    @Test
    fun test03_failed_to_save_note_to_remote() = runTest {

        preConditionCheck()

        val repo = AppRepository(remoteRepository = remoteRepo)

        // Check that no data
        verifyDBIsEmpty(repo)

        mockedStoreServiceStoreResult = false

        val note = Notes(content = "some content", userId = "userId", time = "time")

        var id = -1L

        // Will not return unless the task is completed
        coroutineScope {
            repo.saveNote(scope = this, note) {
                Log.i(tag, "test03_failed_to_save_note_to_remote: callback is called")
                id = it
            }
        }

        // Note metadata must have pendingUpdate set to true

        val metaDb = LocalNoteDatabase.accessNoteMetadata()
        val metadataList = metaDb.getAllMetadata().first()

        assertThat(metadataList.size).isEqualTo(1)

        val metadata = metadataList.first()

        assertThat(metadata.pendingDelete).isFalse()
        assertThat(metadata.uid > 0).isTrue()
        assertThat(metadata.original == id).isTrue()

        // Will not return unless the task is completed
        coroutineScope {
            repo.deleteNote(scope = this, Notes(id = id), {})
        }

        // Metadata and notes should be deleted

        val metadataListAfterDeletion = metaDb.getAllMetadata().first()

        assertThat(metadataListAfterDeletion).isEmpty()

        verifyDBIsEmpty(repo)
        repo.clear()

        postConditionCheck()

        Log.i(tag, "test03_failed_to_save_note_to_remote: done")
    }

    @Test
    fun test04_failed_to_delete_note_in_remote() = runTest {

        preConditionCheck()

        val repo = AppRepository(remoteRepository = remoteRepo)

        // Check that no data
        verifyDBIsEmpty(repo)

        mockedStoreServiceDeleteResult = false

        val note = Notes(content = "some content", userId = "userId", time = "time")

        var id = -1L

        // Will not return unless the task is completed
        coroutineScope {
            repo.saveNote(scope = this, note) {
                Log.i(tag, "test04_failed_to_delete_note_in_remote: callback is called")
                id = it
            }
        }

        // Will not return unless the task is completed
        coroutineScope {
            repo.deleteNote(scope = this, Notes(id = id), {})
        }

        // Note metadata must have pendingDelete set to true

        val metaDb = LocalNoteDatabase.accessNoteMetadata()
        val metadataList = metaDb.getAllMetadata().first()

        assertThat(metadataList.size).isEqualTo(1)

        val metadata = metadataList.first()

        assertThat(metadata.pendingDelete).isTrue()
        assertThat(metadata.uid > 0).isTrue()
        assertThat(metadata.original == id).isTrue()

        // Try retriggering delete from remote
        coroutineScope {
            remoteRepo.updateIfNeeded(this)
        }

        // Still should have pendingDelete set to true

        val metadataListAfterRetrigger = metaDb.getAllMetadata().first()

        assertThat(metadataListAfterRetrigger.size).isEqualTo(1)

        val metadataAfterRetrigger = metadataListAfterRetrigger.first()

        assertThat(metadataAfterRetrigger.pendingDelete).isTrue()
        assertThat(metadataAfterRetrigger.uid > 0).isTrue()
        assertThat(metadataAfterRetrigger.original == id).isTrue()

        // Now we should be able to delete all

        mockedStoreServiceDeleteResult = true

        // Will not return unless the task is completed
        coroutineScope {
            repo.deleteNote(scope = this, Notes(id = id), {})
        }

        // Metadata and notes should be deleted

        val metadataListAfterDeletion = metaDb.getAllMetadata().first()

        assertThat(metadataListAfterDeletion).isEmpty()

        verifyDBIsEmpty(repo)
        repo.clear()

        postConditionCheck()

        Log.i(tag, "test04_failed_to_delete_note_in_remote: done")
    }

    private suspend fun verifyDBIsEmpty(repo: AppRepository) {
        val list = repo.getNotes().first()
        assertThat(list.isEmpty()).isTrue()
    }

}