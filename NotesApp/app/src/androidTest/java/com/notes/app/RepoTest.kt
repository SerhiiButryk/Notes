package com.notes.app

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.notes.notes_ui.NotesViewModel
import com.notes.notes_ui.data.OfflineRepository
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
class RepoTest {

    val appContext: Context = InstrumentationRegistry
        .getInstrumentation().targetContext.applicationContext

    var repo: OfflineRepository? = OfflineRepository()

    @Before
    fun onStart() {
        // Every time when a test runs, new 'RepoTests' object gets created.
        // We get a new 'repo' object. We init it here and destroy in 'onFinish'
        repo?.init(appContext)
    }

    @After
    fun onFinish() {
        repo?.clear()
        repo = null
    }

    @Test
    fun test01_callback_called() = runTest {

        // Check that no data
        val noteList = repo?.getNotes()
            ?.first()
        assertThat(noteList?.isEmpty()).isTrue()

        val note = NotesViewModel.Notes(content = "some content", userId = "userId", time = "time")

        val callbackCalled = AtomicBoolean(false)
        var id = -1L

        // Will not return unless the task is completed
        coroutineScope {
            repo?.saveNote(scope = this, note) {
                Log.i("RepoTests", "test01_callback_called: callback is called")
                // Should be called when a record is added
                callbackCalled.store(true)
                id = it
            }
        }

        // Check that 1 note
        val noteListAfter = repo?.getNotes()
            ?.first()

        assertThat(noteListAfter?.isEmpty()).isFalse()
        assertThat(noteListAfter?.size).isEqualTo(1)

        assertThat(callbackCalled.load()).isTrue()
        assertThat(noteListAfter?.get(0)?.id).isEqualTo(id)

        // Will not return unless the task is completed
        coroutineScope {
            repo?.deleteNote(scope = this, NotesViewModel.Notes(id = id))
        }

        // Check that no data
        val noteListEnd = repo?.getNotes()
            ?.first()
        assertThat(noteListEnd?.isEmpty()).isTrue()

        Log.i("RepoTests", "test01_callback_called: done")
    }

    @Test
    fun test02_callback_not_called() = runTest {

        // Check that no data
        val noteList = repo?.getNotes()
            ?.first()
        assertThat(noteList?.isEmpty()).isTrue()

        val note = NotesViewModel.Notes(content = "some content", userId = "userId", time = "time")

        var id = -1L

        // Will not return unless the task is completed
        coroutineScope {
            repo?.saveNote(scope = this, note) {
                Log.i("RepoTests", "test02_callback_not_called: callback is called")
                // Should be called when a record is added
                id = it
            }
        }

        // Check that 1 note
        val noteListAfter = repo?.getNotes()
            ?.first()

        assertThat(noteListAfter?.isEmpty()).isFalse()
        assertThat(noteListAfter?.size).isEqualTo(1)

        assertThat(noteListAfter?.get(0)?.id).isEqualTo(id)

        val callbackCalled = AtomicBoolean(false)

        // Will not return unless the task is completed
        coroutineScope {
            repo?.saveNote(scope = this, note.copy(id = id, content = "new content")) {
                callbackCalled.store(true)
            }
        }

        assertThat(callbackCalled.load()).isFalse()

        // Check that still one note not more
        val noteListAfterUpdate = repo?.getNotes()
            ?.first()

        assertThat(noteListAfterUpdate?.isEmpty()).isFalse()
        assertThat(noteListAfterUpdate?.size).isEqualTo(1)

        val updatedNote = repo?.getNotes(id = id)!!.first()!!

        with(updatedNote) {
            assertThat(this.content).isEqualTo("new content")
            assertThat(this.id).isEqualTo(id)
            assertThat(this.userId).isEqualTo("userId")
        }

        // Will not return unless the task is completed
        coroutineScope {
            repo?.deleteNote(scope = this, NotesViewModel.Notes(id = id))
        }

        // Check that no notes left
        val noteListEnd = repo?.getNotes()
            ?.first()
        assertThat(noteListEnd?.isEmpty()).isTrue()

        Log.i("RepoTests", "test02_callback_not_called: done")
    }

}