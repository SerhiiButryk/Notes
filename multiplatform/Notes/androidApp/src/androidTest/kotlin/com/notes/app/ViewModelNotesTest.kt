package com.notes.app

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import api.AppService
import api.data.AbstractStorageService
import api.data.Attachments
import api.data.Document
import api.data.Notes
import api.repo.Repository
import com.google.common.truth.Truth.assertThat
import com.notes.notes_ui.NotesViewModel
import com.notes.repo.AndroidSyncManager
import com.notes.repo.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

private const val tag = "ViewModelNotesTest"

@OptIn(ExperimentalAtomicApi::class)
@RunWith(AndroidJUnit4::class)
class ViewModelNotesTest {
    val appContext: Context =
        InstrumentationRegistry
            .getInstrumentation()
            .targetContext.applicationContext

    var viewModel: NotesViewModel? = null

    val cleared = AtomicBoolean(false)

    val note3 = Notes("test3", 3, "userid3", "time3")

    val fakeNotes =
        listOf(
            Notes("test1", 1, "userid1", "time1"),
            Notes("test2", 2, "userid2", "time2"),
            note3,
        )

    @Before
    fun onStart() {
        val repo =
            object : Repository {
                override fun getNotes(): Flow<List<Notes>> {
                    Log.i("ViewModelNotesTests", "getNotes()")
                    return flow {
                        Log.i("ViewModelNotesTests", "getNotes() flow started")
                        emit(fakeNotes)
                    }
                }

                override fun getNotes(id: Long): Flow<Notes?> = emptyFlow()

                override fun saveNote(
                    note: Notes,
                    onAdded: suspend (Notes?) -> Unit,
                ) {
                }

                override fun deleteNote(
                    note: Notes,
                    onDeleted: (Long) -> Unit,
                ) {}

                override fun clear() {
                    Log.i("ViewModelNotesTests", "clear()")
                    cleared.store(true)
                }

                override suspend fun onPasswordChanged() {
                }

                override suspend fun canChangePassword(): Boolean = false

                override suspend fun clearLocalAppStorage() {
                }

                override suspend fun isDataInSync(): Boolean = false

                override fun getAttachments(): Flow<Attachments> = flow { }
            }

        viewModel = NotesViewModel(appRepository = repo)
    }

    @After
    fun onFinish() {
        assertThat(cleared.load()).isFalse()
        val onClear = viewModel!!::class.memberFunctions.find { it.name == "onCleared" }
        if (onClear != null) {
            onClear.isAccessible = true
            onClear.call(viewModel)
        }
        assertThat(cleared.load()).isTrue()
        viewModel = null
    }

    @Test
    fun test01_collect_notesState() =
        runTest {
            val actualList = viewModel?.notesState?.value
            assertThat(actualList?.collection).isEqualTo(emptyList<Notes>())

            val notes = Channel<List<Notes>>(capacity = Channel.CONFLATED)

            // Trigger 'notesState' sharing
            launch(Dispatchers.IO) {
                viewModel?.notesState?.collect {
                    if (it.collection.isNotEmpty()) {
                        notes.send(it.collection)
                        cancel()
                    }
                }
            }

            val actual = notes.receive()
            assertThat(fakeNotes).isEqualTo(actual)
            assertThat(viewModel?.notesState?.value?.collection).isEqualTo(fakeNotes)
        }

    @Test
    fun test02_onSelectAction() =
        runTest {
            val actualNote = viewModel?.noteState?.value
            assertThat(actualNote).isEqualTo(Notes.AbsentNote())

            // Trigger 'notesState' sharing
            val job =
                launch(Dispatchers.IO) {
                    viewModel?.notesState?.collect {
                        if (it.collection.isNotEmpty()) {
                            // Got some valid data. So cancel this coroutine.
                            cancel()
                        }
                    }
                }

            job.join()

            val notes = Channel<Notes>(capacity = Channel.CONFLATED)

            launch(Dispatchers.IO) {
                viewModel?.noteState?.collect {
                    if (it != Notes.AbsentNote()) {
                        notes.send(it)
                        cancel()
                    }
                }
            }

            viewModel?.onSelectAction(Notes(id = note3.id))

            assertThat(notes.receive()).isEqualTo(note3)
        }

    @Test
    fun test03_deletion_verify() =
        runTest {

            val syncManager = AndroidSyncManager(appContext)

            val list = Collections.synchronizedList(ArrayList<Notes>())

            val note1 = Notes(id = 1, content = "test04_test1", userId = "test04_userid1", time = "test04_time1")
            val note2 = Notes(id = 2, content = "test04_test2", userId = "test04_userid2", time = "test04_time2")

            coroutineScope {

                val documents = listOf(
                    Document(name = note1.id.toString(), data = note1.content),
                    Document(name = note2.id.toString(), data = note2.content),
                )

                val localRepo = createAppRepo(
                    setDelete = false,
                    syncManager = syncManager,
                    scope = this,
                    docsList = documents,
                )

                localRepo.saveNote(note = note1, onAdded = {})
                localRepo.saveNote(note = note2, onAdded = {})

                val viewModel = createViewModel(localRepo, backgroundScope)

                launch(Dispatchers.IO) {
                    viewModel.notesState.collect {
                        Log.i(tag, "test03_deletion_verify: first got = $it")
                        if (it.collection.size == 2) {
                            list.add(it.collection[0])
                            list.add(it.collection[1])
                            cancel() // Done!
                        }
                    }
                }

            }

            assertThat(list.size == 2).isTrue()

            var deletedNoteId = 0L

            coroutineScope {

                val noteToDelete = list[0] // Delete first note
                deletedNoteId = noteToDelete.id

                val localRepo = createAppRepo(
                    setDelete = true,
                    syncManager = syncManager,
                    scope = this,
                    docsList = listOf(Document(name = list[1].id.toString()))
                )

                localRepo.deleteNote(noteToDelete, {})

                val viewModel = createViewModel(localRepo, backgroundScope)

                list.clear()

                launch(Dispatchers.IO) {
                    viewModel.notesState.collect {
                        Log.i(tag,"test03_deletion_verify: second got = $it")
                        if (it.collection.size == 1) {
                            list.add(it.collection[0])
                            cancel() // Done!
                        }
                    }
                }

            }

            assertThat(list.size == 1).isTrue()
            assertThat(list[0].id != deletedNoteId).isTrue()

            Log.i(tag,"test03_deletion_verify() done")
        }

    private fun createViewModel(
        repo: AppRepository,
        scope: CoroutineScope,
    ): NotesViewModel {
        val viewModel =
            NotesViewModel(
                appRepository = repo,
                scopeOverride = scope,
            )
        return viewModel
    }

    private fun createAppRepo(
        setDelete: Boolean,
        syncManager: AndroidSyncManager,
        scope: CoroutineScope? = null,
        docsList: List<Document> = emptyList(),
    ): AppRepository {
        val mockedStoreService =
            object : AbstractStorageService() {
                override val key: Any = AppService.FIREBASE_STORAGE

                init {
                    canUse = true
                }

                override suspend fun store(document: Document): Boolean = true

                override suspend fun load(document: Document): Document = Document("", "")

                override suspend fun delete(document: Document): Boolean = setDelete

                override suspend fun fetchAll(): List<Document> {
                    Log.i(tag,"fetchAll() returning ${docsList.size}")
                    return docsList
                }
            }

        return AppRepository.create(listOf(mockedStoreService), syncManager, scope)
    }
}
