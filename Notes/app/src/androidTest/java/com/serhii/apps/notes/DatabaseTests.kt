/**
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Unit Tests for [com.serhii.apps.notes.database.NotesDatabaseProvider] class
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DatabaseTests {

    companion object {

        private val TAG: String = DatabaseTests::class.java.simpleName

        @BeforeClass
        @JvmStatic
        fun oneTimeSetup() {
            Log.i(TAG, "oneTimeSetup()")
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            // Init database
            UserNotesDatabase.init(context)
        }

        @AfterClass
        @JvmStatic
        fun cleanup() {
            Log.i(TAG, "cleanup()")
            UserNotesDatabase.close()
        }

    }

    @Before
    fun setup() {
        Log.i(TAG, "setup()")

        val notesDatabase = UserNotesDatabase

        // Delete records
        val records = UserNotesDatabase.getRecords()
        for (record in records) {
            notesDatabase.deleteRecord(record.id)
        }
    }

    @After
    fun tearDown() {
        Log.i(TAG, "tearDown()")
    }

    /**
     *  Verify that data is saved and retrieved successfully
     *
     *  1. Creates a note
     *  2. Saves it to secure database
     *  3. Retrieves it from database
     *  4. Verifies the results
     */
    @Test
    fun test01_AddRecord() {
        Log.i(TAG, "test01_AddRecord() IN")

        val notesDatabase = UserNotesDatabase
        Assert.assertTrue("Database is not empty", notesDatabase.recordsCount == 0)

        val noteText = "test note"
        val noteTitle = "test note title"

        val note = NoteModel(noteText, noteTitle)

        val id = notesDatabase.addRecord(note)

        Assert.assertEquals("Note wasn't saved in database", notesDatabase.recordsCount, 1)

        val retrievedNote: NoteModel = notesDatabase.getRecord(id.toString())

        Assert.assertEquals("Note is not correct", retrievedNote.note, noteText)
        Assert.assertEquals("Note title is not correct", retrievedNote.title, noteTitle)

        Log.i(TAG, "test01_AddRecord() OUT")
    }

}