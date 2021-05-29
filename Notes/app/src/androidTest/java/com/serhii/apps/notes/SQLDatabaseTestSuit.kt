package com.serhii.apps.notes

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.apps.notes.common.AppConstants
import com.serhii.apps.notes.database.NotesDatabaseProvider
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log
import com.serhii.core.security.Cipher
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SQLDatabaseTestSuit {

    @Before
    fun setup() {
        Log.setTag("AppTests")

        Log.info(TAG, "setup() IN")

        val cipher = Cipher()
        cipher.createKey(AppConstants.SECRET_KEY_DATA_ENC_ALIAS, false)

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        val notesDatabase = NotesDatabaseProvider(context)
        notesDatabase.init(context)

        Log.info(TAG, "setup() OUT")
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

        Log.info(TAG, "test01_SimpleTest() IN")

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

        Log.info(TAG, "test01_SimpleTest() OUT")
    }

    @After
    fun tearDown() {
        Log.info(TAG, "tearDown() IN")

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        val notesDatabase = NotesDatabaseProvider(context)
        notesDatabase.close()

        Log.info(TAG, "tearDown() OUT")
    }

    companion object {
        val TAG = SQLDatabaseTestSuit::class.java.simpleName
    }

}