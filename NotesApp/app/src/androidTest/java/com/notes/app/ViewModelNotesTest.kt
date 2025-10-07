package com.notes.app

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.notes.notes_ui.NotesViewModel
import com.notes.notes_ui.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
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

    val note3 = NotesViewModel.Notes("test3", 3, "userid3", "time3")

    val fakeNotes = listOf<NotesViewModel.Notes>(
        NotesViewModel.Notes("test1", 1, "userid1", "time1"),
        NotesViewModel.Notes("test2", 2, "userid2", "time2"),
        note3,
    )

    @Before
    fun onStart() {

        val repo = object : Repository {
            override fun getNotes(): Flow<List<NotesViewModel.Notes>> {
                Log.i("ViewModelNotesTests", "getNotes()")
                return flow {
                    Log.i("ViewModelNotesTests", "getNotes() flow started")
                    emit(fakeNotes)
                }
            }

            override fun getNotes(id: Long): Flow<NotesViewModel.Notes?> {
                return emptyFlow()
            }

            override fun saveNote(
                note: NotesViewModel.Notes,
                onNewAdded: suspend (Long) -> Unit
            ) {
            }

            override fun deleteNote(note: NotesViewModel.Notes) {
            }

            override fun init(context: Context) {
            }

            override fun clear() {
                Log.i("ViewModelNotesTests", "clear()")
                cleared.store(true)
            }
        }

        viewModel = NotesViewModel(repo)
        viewModel?.init(appContext)
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
    }

    @Test
    fun test01_collect_notesState_test() = runTest {

        val actualList = viewModel?.notesState?.value
        assertThat(actualList).isEqualTo(emptyList<NotesViewModel.Notes>())

        val notes = Channel<List<NotesViewModel.Notes>>(capacity = Channel.CONFLATED)

        // Trigger 'notesState' sharing
        launch(Dispatchers.IO) {
            viewModel?.notesState?.collect {
                Log.i("ViewModelNotesTests", "test01_collect_notesState_test() - $it")
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
    fun test02_onSelectAction_test() = runTest {

        val actualNote = viewModel?.noteState?.value
        assertThat(actualNote).isEqualTo(NotesViewModel.Notes.AbsentNote())

        // Trigger 'notesState' sharing
        val job = launch(Dispatchers.IO) {
            viewModel?.notesState?.collect {
                Log.i("ViewModelNotesTests", "test02_onSelectAction_test() - $it")
                if (it.isNotEmpty()) {
                    // Got some valid data. So cancel this coroutine.
                    cancel()
                }
            }
        }

        job.join()

        val notes = Channel<NotesViewModel.Notes>(capacity = Channel.CONFLATED)

        launch(Dispatchers.IO) {
            viewModel?.noteState?.collect {
                Log.i("ViewModelNotesTests", "test02_onSelectAction_test() - collecting 'noteState' - $it")
                if (it != NotesViewModel.Notes.AbsentNote()) {
                    notes.send(it)
                    cancel()
                }
            }
        }

        viewModel?.onSelectAction(NotesViewModel.Notes(id = note3.id))

        assertThat(notes.receive()).isEqualTo(note3)

    }

    @Test
    fun test03_onNewAdded_test() = runTest {

        val actualNote = viewModel?.noteState?.value
        assertThat(actualNote).isEqualTo(NotesViewModel.Notes.AbsentNote())

        // Trigger 'notesState' sharing
        val job = launch(Dispatchers.IO) {
            viewModel?.notesState?.collect {
                Log.i("ViewModelNotesTests", "test03_onNewAdded_test() - $it")
                if (it.isNotEmpty()) {
                    // Got some valid data. So cancel this coroutine.
                    cancel()
                }
            }
        }

        job.join()

        val notes = Channel<NotesViewModel.Notes>(capacity = Channel.CONFLATED)

        launch(Dispatchers.IO) {
            viewModel?.noteState?.collect {
                Log.i("ViewModelNotesTests", "test03_onNewAdded_test() - collecting 'noteState' - $it")
                if (it != NotesViewModel.Notes.AbsentNote()) {
                    notes.send(it)
                    cancel()
                }
            }
        }

        viewModel?.onAdded(id = note3.id)

        assertThat(notes.receive()).isEqualTo(note3)

    }

}