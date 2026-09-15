package com.notes.app

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import api.AppService
import api.data.AbstractStorageService
import api.data.Document
import api.data.Notes
import com.google.common.truth.Truth.assertThat
import com.notes.repo.AndroidSyncManager
import com.notes.repo.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalAtomicApi::class)
@RunWith(AndroidJUnit4::class)
class RepoTest : AppTest() {
    private val appContext: Context =
        InstrumentationRegistry
            .getInstrumentation()
            .targetContext.applicationContext

    private val tag = "RepoTest"

    private val syncManager = AndroidSyncManager(appContext)

    private fun createAppRepo(
        syncManager: AndroidSyncManager,
        scope: CoroutineScope? = null,
        setDelete: Boolean = true,
        setStore: Boolean = true,
        docsList: List<Document> = emptyList(),
    ): AppRepository {
        val mockedStoreServiceGoogle = object : AbstractStorageService() {
            override val key: Any = AppService.GOOGLE_STORAGE

            init {
                canUse = true
            }

            override suspend fun store(document: Document): Boolean = setStore

            override suspend fun load(document: Document): Document = Document("", "")

            override suspend fun delete(document: Document): Boolean = setDelete

            override suspend fun fetchAll(): List<Document> = emptyList()
        }
        val mockedStoreServiceFirebase = object : AbstractStorageService() {
            override val key: Any = AppService.FIREBASE_STORAGE

            init {
                canUse = true
            }

            override suspend fun store(document: Document): Boolean = setStore

            override suspend fun load(document: Document): Document = Document("", "")

            override suspend fun delete(document: Document): Boolean = setDelete

            override suspend fun fetchAll(): List<Document> = docsList
        }
        return AppRepository.create(
            listOf(mockedStoreServiceGoogle, mockedStoreServiceFirebase),
            syncManager,
            scope
        )
    }

    @Before
    fun onStart() {
    }

    @After
    fun onFinish() {
    }

    @Test
    fun test01_insert_new_note() =
        runTest {
            preConditionCheck()

            val repo = createAppRepo(syncManager = syncManager)

            // Check that no data
            verifyDBIsEmpty(repo)

            val note = Notes(content = "some content", userId = "userId", time = "time")

            val callbackCalled = AtomicBoolean(false)
            var id = -1L

            // Will not return unless the task is completed
            coroutineScope {
                val documents = listOf(
                    Document(name = note.id.toString(), data = note.content),
                )
                val localRepo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                    docsList = documents,
                )
                localRepo.saveNote(note, onAdded = {
                    Log.i(tag, "test01_insert_new_note: callback is called id = $it")
                    // Should be called when a record is added
                    callbackCalled.store(true)
                    id = it
                })
            }

            // Check that 1 note
            assertThat(syncManager.notes.first().size == 1).isTrue()
            assertThat(syncManager.notes.first()[0].id).isEqualTo(id)
            assertThat(syncManager.notes.first()[0].content).isEqualTo(note.content)

            val noteListAfter = repo.getNotes().first()

            assertThat(noteListAfter.isEmpty()).isFalse()
            assertThat(noteListAfter.size).isEqualTo(1)

            assertThat(callbackCalled.load()).isTrue()
            assertThat(noteListAfter[0].id).isEqualTo(id)

            // Will not return unless the task is completed
            coroutineScope {
                val localRepo = createAppRepo(syncManager = syncManager, scope = this)
                localRepo.deleteNote(Notes(id = id), {})
            }

            // Check that no data
            verifyDBIsEmpty(repo)

            postConditionCheck()

            Log.i(tag, "test01_insert_new_note: done")
        }

    @Test
    fun test02_update_existed_note() =
        runTest {
            preConditionCheck()

            val repo = createAppRepo(syncManager = syncManager)

            // Check that no data
            verifyDBIsEmpty(repo)

            val note = Notes(content = "some content", userId = "userId", time = "time")

            var id = -1L

            // Will not return unless the task is completed
            coroutineScope {
                val documents = listOf(
                    Document(name = note.id.toString(), data = note.content),
                )
                val localRepo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                    docsList = documents,
                )
                localRepo.saveNote(note) {
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
                val copy = note.copy(id = id, content = "new content")
                val documents = listOf(
                    Document(name = copy.id.toString(), data = copy.content),
                )
                val localRepo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                    docsList = documents,
                )
                localRepo.saveNote(copy) {
                    callbackCalled.store(true)
                }
            }

            assertThat(callbackCalled.load()).isTrue()

            // Check that still one note not more
            val noteListAfterUpdate = repo.getNotes().first()

            assertThat(noteListAfterUpdate.isEmpty()).isFalse()
            assertThat(noteListAfterUpdate.size).isEqualTo(1)

            val updatedNote = repo.getNotes(id = id).first()!!

            with(updatedNote) {
                assertThat(this.content).isEqualTo("new content")
                assertThat(this.id).isEqualTo(id)
            }

            assertThat(syncManager.notes.first().size == 1).isTrue()
            assertThat(syncManager.notes.first()[0].content).isEqualTo("new content")
            assertThat(syncManager.notes.first()[0].id).isEqualTo(id)

            // Will not return unless the task is completed
            coroutineScope {
                val localRepo = createAppRepo(syncManager = syncManager, scope = this)
                localRepo.deleteNote(Notes(id = id), {})
            }

            // Check that no data
            verifyDBIsEmpty(repo)

            repo.clear()

            postConditionCheck()

            Log.i(tag, "test02_update_existed_note: done")
        }

    @Test
    fun test03_failed_to_save_note_to_remote() =
        runTest(timeout = 10.minutes) {
            preConditionCheck()

            val repo = createAppRepo(syncManager = syncManager)

            // Check that no data
            verifyDBIsEmpty(repo)

            val note = Notes(id = 1, content = "some content", userId = "userId", time = "time")

            var id = -1L

            // Will not return unless the task is completed
            coroutineScope {
                val localRepo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                    setStore = false,
                )
                localRepo.saveNote(note) {
                    Log.i(tag, "test03_failed_to_save_note_to_remote: callback is called")
                    id = it
                }
            }

            // Note metadata must have pendingUpdate set to true

            val metadataList = syncManager.__getMetadata_FOR_TEST()

            assertThat(metadataList.size).isEqualTo(1)

            val metadata = metadataList.first()

            assertThat(metadata.pendingDelete).isFalse()
            assertThat(metadata.id > 0).isTrue()
            assertThat(metadata.noteId == id).isTrue()

            // Should be false
            assertThat(syncManager.isAllInSync()).isFalse()

            // Will not return unless the task is completed
            coroutineScope {
                val localRepo = createAppRepo(syncManager = syncManager, scope = this)
                localRepo.deleteNote(Notes(id = id), {})
            }

            // Metadata and notes should be deleted

            val metadataListAfterDeletion = syncManager.__getMetadata_FOR_TEST()

            assertThat(metadataListAfterDeletion).isEmpty()

            verifyDBIsEmpty(repo)

            postConditionCheck()

            Log.i(tag, "test03_failed_to_save_note_to_remote: done")
        }

    @Test
    fun test04_failed_to_delete_note_in_remote() =
        runTest(timeout = 10.minutes) {
            preConditionCheck()

            val repo = createAppRepo(syncManager = syncManager)

            // Check that no data
            verifyDBIsEmpty(repo)

            val note = Notes(id = 1, content = "some content", userId = "userId", time = "time")

            var id = -1L

            // Will not return unless the task is completed
            coroutineScope {
                val localRepo = createAppRepo(syncManager = syncManager, scope = this)
                localRepo.saveNote(note) {
                    Log.i(tag, "test04_failed_to_delete_note_in_remote: callback is called")
                    id = it
                }
            }

            // Will not return unless the task is completed
            coroutineScope {
                val localRepo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                    setDelete = false,
                )
                localRepo.deleteNote(Notes(id = id), {})
            }

            // Note metadata must have pendingDelete set to true

            val metadataList = syncManager.__getMetadata_FOR_TEST()

            assertThat(metadataList.size).isEqualTo(1)

            val metadata = metadataList.first()

            assertThat(metadata.pendingDelete).isTrue()
            assertThat(metadata.id > 0).isTrue()
            assertThat(metadata.noteId == id).isTrue()

            // Try retriggering delete from remote
            coroutineScope {
                repo.syncData(this)
            }

            // Still should have pendingDelete set to true

            val metadataListAfterRetrigger = syncManager.__getMetadata_FOR_TEST()

            assertThat(metadataListAfterRetrigger.size).isEqualTo(1)

            val metadataAfterRetrigger = metadataListAfterRetrigger.first()

            assertThat(metadataAfterRetrigger.pendingDelete).isTrue()
            assertThat(metadataAfterRetrigger.id > 0).isTrue()
            assertThat(metadataAfterRetrigger.noteId == id).isTrue()

            // Will not return unless the task is completed
            coroutineScope {
                val localRepo = createAppRepo(syncManager = syncManager, scope = this)
                localRepo.deleteNote(Notes(id = id), {})
            }

            // Metadata and notes should be deleted

            val metadataListAfterDeletion = syncManager.__getMetadata_FOR_TEST()

            assertThat(metadataListAfterDeletion).isEmpty()

            verifyDBIsEmpty(repo)

            postConditionCheck()

            Log.i(tag, "test04_failed_to_delete_note_in_remote: done")
        }

    private suspend fun verifyDBIsEmpty(repo: AppRepository) {
        val list = repo.getNotes().first()
        assertThat(list.isEmpty()).isTrue()
        assertThat(syncManager.notes.first().isEmpty()).isTrue()
    }
}
