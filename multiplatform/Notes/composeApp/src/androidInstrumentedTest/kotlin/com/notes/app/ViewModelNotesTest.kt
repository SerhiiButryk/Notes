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
import com.notes.notes_ui.NotesViewModel
import com.notes.notes_ui.Repository
import com.notes.notes_ui.data.AppRepository
import com.notes.notes_ui.data.RemoteRepository
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
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible

@OptIn(ExperimentalAtomicApi::class)
@RunWith(AndroidJUnit4::class)
class ViewModelNotesTest {

    val appContext: Context = InstrumentationRegistry
        .getInstrumentation().targetContext.applicationContext

    var viewModel: NotesViewModel? = null

    val cleared = AtomicBoolean(false)

    val note3 = Notes("test3", 3, "userid3", "time3")

    val fakeNotes = listOf(
        Notes("test1", 1, "userid1", "time1"),
        Notes("test2", 2, "userid2", "time2"),
        note3,
    )

    @Before
    fun onStart() {

        LocalNoteDatabase.initialize(appContext)

        val repo = object : Repository {
            override fun getNotes(): Flow<List<Notes>> {
                Log.i("ViewModelNotesTests", "getNotes()")
                return flow {
                    Log.i("ViewModelNotesTests", "getNotes() flow started")
                    emit(fakeNotes)
                }
            }

            override fun getNotes(id: Long): Flow<Notes?> {
                return emptyFlow()
            }

            override fun saveNote(
                note: Notes,
                onNewAdded: suspend (Long) -> Unit
            ) {
            }

            override fun deleteNote(note: Notes, callback: (Long) -> Unit) {}

            override fun clear() {
                Log.i("ViewModelNotesTests", "clear()")
                cleared.store(true)
            }
        }

        viewModel = NotesViewModel(repo)
    }

    @After
    fun onFinish() {
        assertThat(cleared.load()).isFalse()
        val onClear = viewModel!!::class.declaredMemberFunctions.find { it.name == "onCleared" }
        if (onClear != null) {
            onClear.isAccessible = true
            onClear.call(viewModel)
        }
        assertThat(cleared.load()).isTrue()
        viewModel = null
        LocalNoteDatabase.close()
    }

    @Test
    fun test01_collect_notesState() = runTest {

        val actualList = viewModel?.notesState?.value
        assertThat(actualList).isEqualTo(emptyList<Notes>())

        val notes = Channel<List<Notes>>(capacity = Channel.CONFLATED)

        // Trigger 'notesState' sharing
        launch(Dispatchers.IO) {
            viewModel?.notesState?.collect {
                if (it.isNotEmpty()) {
                    notes.send(it)
                    cancel()
                }
            }
        }

        assertThat(fakeNotes).isEqualTo(notes.receive())
        assertThat(viewModel?.notesState?.value).isEqualTo(fakeNotes)
    }

    @Test
    fun test02_onSelectAction() = runTest {

        val actualNote = viewModel?.noteState?.value
        assertThat(actualNote).isEqualTo(Notes.AbsentNote())

        // Trigger 'notesState' sharing
        val job = launch(Dispatchers.IO) {
            viewModel?.notesState?.collect {
                if (it.isNotEmpty()) {
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
    fun test03_onNewAdded() = runTest {

        val actualNote = viewModel?.noteState?.value
        assertThat(actualNote).isEqualTo(Notes.AbsentNote())

        // Trigger 'notesState' sharing
        val job = launch(Dispatchers.IO) {
            viewModel?.notesState?.collect {
                if (it.isNotEmpty()) {
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

        viewModel?.onNoteAdded(id = note3.id)

        assertThat(notes.receive()).isEqualTo(note3)

    }

    @Test
    fun test04_collect_notesState_and_deletion_in_remote_failed() = runTest {

        val realRepo = setupRealRemoteRepo(setDelete = false)
        val realVM = setupVMWithRealRepo(realRepo, backgroundScope)

        val notes = Channel<List<Notes>>(capacity = Channel.CONFLATED)

        // Trigger 'notesState' sharing
        // backgroundScope is used to not wait for coroutine completion
        backgroundScope.launch(Dispatchers.IO) {
            realVM.notesState.collect {
                Log.i("ViewModelNotesTest",
                    "test04_collect_notesState_and_deletion_in_remote_failed: got = $it")
                notes.send(it)
            }
        }

        val note1 = Notes(content = "test04_test1", userId = "test04_userid1", time = "test04_time1")
        val note2 = Notes(content = "test04_test2", userId = "test04_userid2", time = "test04_time2")

        coroutineScope {
            realRepo.saveNote(this, note1, {})
            realRepo.saveNote(this, note2, {})
        }

        Thread.sleep(500) // Wait sometime for data, can't use delay()
        // cos it operates in virtual time

        val list1 = notes.receive()

        assertThat(list1.size == 2).isTrue()

        var deletedNoteId = 0L

        coroutineScope {

            val noteToDelete = list1[0] // Delete first note
            deletedNoteId = noteToDelete.id

            realRepo.deleteNote(this, noteToDelete, {})
        }

        val list2 = notes.receive() // New data received

        assertThat(list2.size == 1).isTrue()
        assertThat(list2[0].id != deletedNoteId).isTrue()

        notes.cancel()

        // Clear VM
        val onClear = realVM::class.declaredMemberFunctions.find { it.name == "onCleared" }
        if (onClear != null) {
            onClear.isAccessible = true
            onClear.call(realVM)
        }

        Log.i(
            "ViewModelNotesTests",
            "test04_collect_notesState_and_deletion_in_remote_failed() done"
        )

    }

    private fun setupVMWithRealRepo(repo: AppRepository, scope: CoroutineScope): NotesViewModel {
        val viewModel = NotesViewModel(repo, scope)
        return viewModel
    }

    private fun setupRealRemoteRepo(setDelete: Boolean): AppRepository {

        val mockedStoreService = object : AbstractStorageService() {

            override val name: String = "firebase"

            override suspend fun store(document: Document): Boolean {
                return true
            }

            override suspend fun load(name: String): Document? {
                return Document("", "")
            }

            override suspend fun delete(name: String): Boolean {
                return setDelete
            }

            override suspend fun fetchAll(): List<Document> {
                return emptyList()
            }

        }

        return AppRepository(RemoteRepository(listOf(mockedStoreService)))
    }

}