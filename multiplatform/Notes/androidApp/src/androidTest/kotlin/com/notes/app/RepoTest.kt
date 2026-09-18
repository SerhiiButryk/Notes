package com.notes.app

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import api.data.Document
import api.data.Notes
import com.google.common.truth.Truth.assertThat
import com.notes.repo.AndroidSyncManager
import com.notes.repo.FilesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    private val appContext: Context =
        InstrumentationRegistry
            .getInstrumentation()
            .targetContext.applicationContext

    private val tag = "RepoTest"

    private val syncManager = AndroidSyncManager(appContext)

    @Before
    fun onStart() {
    }

    @After
    fun onFinish() {
    }

    @Test
    fun test01_insert_new_note() =
        runTest {
            Log.i(tag, "test01_insert_new_note: started")

            preConditionCheck()

            // Check that no data
            verifyDBIsEmpty(syncManager, this)

            val note = Notes(id = 1, content = "some content", userId = "userId", time = "time")

            val callbackCalled = AtomicBoolean(false)
            var notesSaved: Notes? = null

            // Will not return unless the child coroutines have been completed
            coroutineScope {

                val documents = listOf(
                    Document(name = note.id.toString(), data = note.content),
                )

                setupServices(
                    docsList = documents,
                )

                val localRepo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                )

                localRepo.saveNote(note, onAdded = {
                    // Should be called when a record is added
                    callbackCalled.store(true)
                    notesSaved = it
                })

            }

            // Check that 1 note
            val actualList = syncManager.notes.first()
            assertThat(actualList.size == 1).isTrue()
            assertThat(actualList[0].id).isEqualTo(notesSaved?.id)
            assertThat(actualList[0].content).isEqualTo(note.content)

            run<Unit> {

                val repo = createAppRepo(syncManager = syncManager)
                val noteListAfter = repo.getNotes().first()

                assertThat(noteListAfter.isEmpty()).isFalse()
                assertThat(noteListAfter.size).isEqualTo(1)

                assertThat(callbackCalled.load()).isTrue()
                assertThat(noteListAfter[0].id).isEqualTo(notesSaved?.id)

            }

            // Will not return unless the child coroutines have been completed
            coroutineScope {

                setupServices(docsList = emptyList())

                val repo = createAppRepo(syncManager = syncManager, scope = this)

                repo.deleteNote(Notes(id = notesSaved!!.id), {})
            }

            assertThat(syncManager.notes.first().isEmpty()).isTrue()

            run {
                val repo = createAppRepo(syncManager = syncManager)
                assertThat(repo.getNotes().first().isEmpty()).isTrue()
            }

            postConditionCheck()

            Log.i(tag, "test01_insert_new_note: done")
        }

    @Test
    fun test02_update_existed_note() =
        runTest {
            Log.i(tag, "test02_update_existed_note: started")

            preConditionCheck()

            val repo = createAppRepo(syncManager = syncManager)

            // Check that no data
            verifyDBIsEmpty(syncManager, this)

            val note = Notes(content = "some content", userId = "userId", time = "time")

            var notesSaved: Notes? = null

            val channel = Channel<List<Notes>>(capacity = Channel.CONFLATED)
            backgroundScope.launch {
                repo.getNotes().collect {
                    channel.send(it)
                }
            }

            // Will not return unless the child coroutines have been completed
            coroutineScope {
                val documents = listOf(
                    Document(name = note.id.toString(), data = note.content),
                )
                setupServices(
                    docsList = documents,
                )
                val localRepo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                )
                localRepo.saveNote(note) {
                    Log.i(tag, "test02_update_existed_note: callback is called")
                    // Should be called when a record is added
                    notesSaved = it
                }
            }

            // Check that 1 note
            val noteListAfter = channel.receive()

            assertThat(noteListAfter.isEmpty()).isFalse()
            assertThat(noteListAfter.size).isEqualTo(1)

            assertThat(noteListAfter[0].id).isEqualTo(notesSaved?.id)

            val callbackCalled = AtomicBoolean(false)

            // Will not return unless the child coroutines have been completed
            coroutineScope {
                val copy = note.copy(id = notesSaved!!.id, content = "new content")
                val documents = listOf(
                    Document(name = copy.id.toString(), data = copy.content),
                )
                setupServices(
                    docsList = documents,
                )
                val localRepo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                )
                localRepo.saveNote(copy) {
                    callbackCalled.store(true)
                }
            }

            assertThat(callbackCalled.load()).isTrue()

            // Check that still one note not more
            val noteListAfterUpdate = channel.receive()

            assertThat(noteListAfterUpdate.isEmpty()).isFalse()
            assertThat(noteListAfterUpdate.size).isEqualTo(1)

            with(noteListAfterUpdate[0]) {
                assertThat(this.content).isEqualTo("new content")
                assertThat(this.id).isEqualTo(id)
            }

            // Will not return unless the child coroutines have been completed
            coroutineScope {
                setupServices(docsList = emptyList())
                val localRepo = createAppRepo(syncManager = syncManager, scope = this)
                localRepo.deleteNote(Notes(id = notesSaved!!.id), {})
            }

            val afterDeletion = channel.receive()
            assertThat(afterDeletion.isEmpty()).isTrue()

            // Check that no data
            verifyDBIsEmpty(syncManager, this)

            postConditionCheck()

            Log.i(tag, "test02_update_existed_note: done")
        }

    @Test
    fun test03_failed_to_save_note_to_remote() =
        runTest {
            Log.i(tag, "test03_failed_to_save_note_to_remote: started")

            preConditionCheck()

            val repo = createAppRepo(syncManager = syncManager)

            // Check that no data
            verifyDBIsEmpty(syncManager, this)

            val note = Notes(id = 1, content = "some content", userId = "userId", time = "time")

            // Will not return unless the child coroutines have been completed
            coroutineScope {
                setupServices(setStore = false)
                val localRepo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                )
                localRepo.saveNote(note) {}
            }

            // Note metadata must have pendingUpdate set to true

            val metadataList = syncManager.__getMetadata_FOR_TEST()

            assertThat(metadataList.size).isEqualTo(1)

            val metadata = metadataList.first()

            assertThat(metadata.pendingDelete).isFalse()
            assertThat(metadata.id > 0).isTrue()

            // Should be false
            assertThat(syncManager.isAllInSync()).isFalse()

            // Will not return unless the child coroutines have been completed
            coroutineScope {
                val localRepo = createAppRepo(syncManager = syncManager, scope = this)
                localRepo.deleteNote(Notes(id = note.id), {})
            }

            // Metadata and notes should be deleted

            val metadataListAfterDeletion = syncManager.__getMetadata_FOR_TEST()

            assertThat(metadataListAfterDeletion).isEmpty()

            verifyDBIsEmpty(syncManager, this)

            postConditionCheck()

            Log.i(tag, "test03_failed_to_save_note_to_remote: done")
        }

    @Test
    fun test04_failed_to_delete_note_in_remote() =
        runTest {
            Log.i(tag, "test04_failed_to_delete_note_in_remote: started")

            preConditionCheck()

            val repo = createAppRepo(syncManager = syncManager)

            // Check that no data
            verifyDBIsEmpty(syncManager, this)

            val note = Notes(id = 1, content = "some content", userId = "userId", time = "time")

            var notesSaved: Notes? = null

            // Will not return unless the child coroutines have been completed
            coroutineScope {
                setupServices(docsList = listOf(Document(name = note.id.toString(), data = note.content)))
                val repo = createAppRepo(syncManager = syncManager, scope = this)
                repo.saveNote(note) {
                    Log.i(tag, "test04_failed_to_delete_note_in_remote: callback is called")
                    notesSaved = it
                }
            }

            assertThat(repo.getNotes().first()).isNotEmpty()
            assertThat(repo.getNotes().first().size == 1).isTrue()

            // Will not return unless the child coroutines have been completed
            coroutineScope {
                setupServices(
                    docsList = listOf(Document(name = note.id.toString(), data = note.content)),
                    setDelete = false
                )
                val repo = createAppRepo(
                    syncManager = syncManager,
                    scope = this,
                )
                repo.deleteNote(Notes(id = notesSaved!!.id), {})
            }

            // Note metadata must have pendingDelete set to true

            val metadataList = syncManager.__getMetadata_FOR_TEST()

            assertThat(metadataList.size).isEqualTo(1)

            val metadata = metadataList.first()

            assertThat(metadata.pendingDelete).isTrue()
            assertThat(metadata.id > 0).isTrue()
            assertThat(metadata.noteId == notesSaved!!.id).isTrue()

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
            assertThat(metadataAfterRetrigger.noteId == notesSaved?.id).isTrue()

            setupServices(
                docsList = emptyList(),
            )

            run {
                val repo = createAppRepo(syncManager = syncManager, scope = backgroundScope)
                repo.syncData()
            }

            val list = repo.getNotes().first()
            if (list.isNotEmpty()) {
                // Wait some time, we might not receive new value at this moment
                for (i in 0..5) {
                    Thread.sleep(300)
                    if (repo.getNotes().first().isEmpty()) break
                }
            }

            // Metadata and notes should be deleted

            val metadataListAfterDeletion = syncManager.__getMetadata_FOR_TEST()

            assertThat(metadataListAfterDeletion).isEmpty()

            verifyDBIsEmpty(syncManager, this)

            postConditionCheck()

            Log.i(tag, "test04_failed_to_delete_note_in_remote: done")
        }

    suspend fun verifyDBIsEmpty(syncManager: AndroidSyncManager, scope: CoroutineScope) {
        val fileManager = FilesManager()
        assertThat(fileManager.readCache(fileManager.secondCacheDir).isEmpty()).isTrue()
        val repo = createAppRepo(syncManager, scope)
        assertThat(repo.getNotes().first().isEmpty()).isTrue()
        assertThat(syncManager.notes.first().isEmpty()).isTrue()
    }
}
