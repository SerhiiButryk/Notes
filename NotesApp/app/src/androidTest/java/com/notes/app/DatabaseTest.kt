package com.notes.app

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.notes.data.DBLifecycleCallback
import com.notes.data.LocalNoteDatabase
import com.notes.data.NoteDatabase
import com.notes.data.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    val appContext: Context = InstrumentationRegistry
        .getInstrumentation().targetContext.applicationContext

    // Every time when a test runs, new 'DatabaseTest' object gets created.
    // 'onCreate' is called only once when db is first created but
    // 'onOpen' is called several time. So, made them static to verify that
    // the below methods are called at least once.
    companion object {
        val created = AtomicBoolean(false)
        val opened = AtomicBoolean(false)
        val closed = AtomicBoolean(false)
    }

    val callback = object : DBLifecycleCallback {

        override fun onCreate() {
            created.store(true)
        }

        override fun onOpen() {
            opened.store(true)
        }

        override fun onClose() {
            closed.store(true)
        }
    }

    @Before
    fun onStart() {
        LocalNoteDatabase.initialize(appContext, callback)
    }

    @After
    fun onFinish() {
        LocalNoteDatabase.close()
    }

    private suspend fun preConditionCheck() {
        assertThat(LocalNoteDatabase.initialize()).isNotNull()
        assertThat(LocalNoteDatabase.access()).isNotNull()
    }

    private suspend fun postConditionCheck() {

        val db = LocalNoteDatabase.access()

        val records = mutableListOf<List<NoteEntity>>()

        db.getNotes()
            .onEach {
                records.add(it)
            }
            .first()

        assertWithMessage("Database is not empty")
            .that(records[0].isEmpty())
            .isTrue()

    }

    @Test
    fun test01_add_delete() = runTest {

        preConditionCheck()

        val db = LocalNoteDatabase.access()

        val note = NoteEntity(1, "user_id", "content", "some_time")
        db.insertNote(note)

        val records = mutableListOf<List<NoteEntity>>()

        db.getNotes()
            .onEach {
                records.add(it)
            }
            .first()

        assertThat(records.size).isEqualTo(1)
        assertThat(records[0].size).isEqualTo(1)

        val actualNote = records[0][0]

        assertThat(actualNote).isEqualTo(note)

        // Make sure that id is enough to delete data
        val noteDelete = NoteEntity(1, "", "", "")
        db.deleteNote(noteDelete)

        records.clear()

        db.getNotes()
            .onEach {
                records.add(it)
            }
            .first()

        // Check that deletion is successful
        assertWithMessage("Database is not empty")
            .that(records[0].isEmpty())
            .isTrue()

        // Check that callbacks are called
        assertThat(created.load()).isTrue()
        assertThat(opened.load()).isTrue()

        postConditionCheck()

        Log.i("DatabaseTest", "test01_add_delete: done")
    }

    @Test
    fun test02_add_multiple() = runTest {

        preConditionCheck()

        val db = LocalNoteDatabase.access()

        val recCount = 4

        val note1 = NoteEntity(1, "user_id", "content", "some_time")
        db.insertNote(note1)

        // Id should be auto-generated
        val note2 = NoteEntity(userId = "user_id", content = "content", time = "some_time")
        db.insertNote(note2)

        // Id should be auto-generated
        val note3 = NoteEntity(userId = "user_id", content = "content", time = "some_time")
        db.insertNote(note3)

        // Id should be auto-generated
        val note4 = NoteEntity(userId = "user_id", content = "content", time = "some_time")
        db.insertNote(note4)

        val records = mutableListOf<List<NoteEntity>>()

        db.getNotes()
            .onEach {
                records.add(it)
            }
            .first()

        assertThat(records.size).isEqualTo(1)
        assertThat(records[0].size).isEqualTo(recCount)

        var expectedId = 1
        for (list in records) {
            list.forEach { rec ->
                assertThat(rec.uid).isEqualTo(expectedId++)
                db.deleteNote(rec)
            }
        }

        // Check that callbacks are called
        assertThat(created.load()).isTrue()
        assertThat(opened.load()).isTrue()

        postConditionCheck()

        Log.i("DatabaseTest", "test02_add_multiple: done")
    }

    @Test
    fun test03_double_create() = runTest {

        preConditionCheck()

        val mutex = Mutex()
        val list = mutableListOf<NoteDatabase>()

        coroutineScope {
            (1..100).map {
                launch(Dispatchers.IO) {
                    val db = LocalNoteDatabase.initialize(appContext)
                    assertThat(db).isNotNull()
                    mutex.withLock {
                        list.add(db!!)
                    }
                }
            }
        }

        // If it contains equal obj ref then it means that we've created db only once
        // otherwise it could mean that we've created it multiple times which is not thread safe
        val first = list.first()
        val res = list.all { it === first }
        assertWithMessage("Looks like we've been not thread safe !")
            .that(res)
            .isTrue()

        postConditionCheck()

        Log.i("DatabaseTest", "test03_double_create: done")
    }

    @Test
    fun test04_initialize_races_case_1() = runTest {

        LocalNoteDatabase.close()

        launch(Dispatchers.IO) {
            LocalNoteDatabase.access()
        }

        launch(Dispatchers.IO) {
            delay(300)
            LocalNoteDatabase.initialize(appContext)
        }

        Log.i("DatabaseTest", "test04_initialize_races_case_1: done")
    }

    @Test
    fun test05_initialize_races_case_2() = runTest {

        LocalNoteDatabase.close()

        launch(Dispatchers.IO) {
            LocalNoteDatabase.initialize(appContext)
        }

        launch(Dispatchers.IO) {
            delay(300)
            LocalNoteDatabase.access()
        }

        Log.i("DatabaseTest", "test05_initialize_races_case_2: done")
    }

}