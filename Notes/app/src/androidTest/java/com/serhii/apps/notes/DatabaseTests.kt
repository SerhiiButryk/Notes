/**
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.apps.notes.common.AppConstants
import com.serhii.apps.notes.database.NotesDatabaseProvider
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.security.Cipher
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Unit Tests for
 * @linkcom.serhii.apps.notes.database.NotesDatabaseProvider
 */

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DatabaseTests {

    @Before
    fun setup() {
        Log.i(TAG, "setup() IN")

        if (!isKeyCreated) {
            Log.i(TAG, "setup() Create keys")
            // Create key for notes data encryption
            val cipher = Cipher()
            cipher.createKey(AppConstants.SECRET_KEY_DATA_ENC_ALIAS, false)
            isKeyCreated = true
        } else {
            Log.i(TAG, "setup() Keys are created")
        }

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        // Init database
        val notesDatabase = NotesDatabaseProvider(context)
        notesDatabase.init(context)

        Log.i(TAG, "setup() OUT")
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
    fun test01_SimpleTest() {

        Log.i(TAG, "test01_SimpleTest() IN")

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        val notesDatabase = NotesDatabaseProvider(context)

        val noteText = "test note";
        val noteTitle = "test note title";

        val note = NoteModel(noteText, noteTitle)

        val id = notesDatabase.addRecord(note)

        Assert.assertEquals("Note wasn't saved in database", notesDatabase.recordsCount, 1)

        val retrievedNote = notesDatabase.getRecord(id.toString())

        Assert.assertEquals("Note is not correct", retrievedNote.note, noteText)
        Assert.assertEquals("Note title is not correct", retrievedNote.title, noteTitle)

        Log.i(TAG, "test01_SimpleTest() OUT")
    }

    @After
    fun tearDown() {
        Log.i(TAG, "tearDown() IN")

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        val notesDatabase = NotesDatabaseProvider(context)
        notesDatabase.close()

        Log.i(TAG, "tearDown() OUT")
    }

    companion object {
        val TAG = DatabaseTests::class.java.simpleName
        var isKeyCreated = false
    }

}