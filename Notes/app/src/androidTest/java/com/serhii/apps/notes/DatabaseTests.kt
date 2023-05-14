/**
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.database.impl.EncryptionHelper
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.security.impl.crypto.CryptoError
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
        fun onetimeSetup() {
            Log.i(TAG, "onetimeSetup()")
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

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        // Delete records
        val records = UserNotesDatabase.getRecords(context)
        for (record in records) {
            notesDatabase.deleteRecord(record.id)
        }
    }

    @After
    fun tearDown() {
        Log.i(TAG, "tearDown() IN")
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

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val id = notesDatabase.addRecord(note, context)

        Assert.assertEquals("Note wasn't saved in database", notesDatabase.recordsCount, 1)

        val retrievedNote: NoteModel = notesDatabase.getRecord(id.toString(), context)

        Assert.assertEquals("Note is not correct", retrievedNote.note, noteText)
        Assert.assertEquals("Note title is not correct", retrievedNote.title, noteTitle)

        Log.i(TAG, "test01_AddRecord() OUT")
    }

    @Test
    fun test02_EncryptionHelper_decrypt_encrypt() {
        Log.i(TAG, "test02_EncryptionHelper_decrypt_encrypt() IN")

        val notesDatabase = UserNotesDatabase
        Assert.assertTrue("Database is not empty", notesDatabase.recordsCount == 0)

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val encryptionHelper = EncryptionHelper(context)

        val text = "Hello this is my note"
        val title = "Hello this is my title"
        val note = NoteModel(text, title)

        val encText: String = encryptionHelper.encrypt(note)

        Assert.assertTrue(
            "Has error during encrypt: " + encryptionHelper.lastError,
            encryptionHelper.lastError == CryptoError.OK
        )
        Assert.assertTrue("Failed to encrypt", encText.isNotEmpty())

        val ivField = encryptionHelper.javaClass.getDeclaredField("ivNote")
        ivField.isAccessible = true
        val byteArrayBeforeSave: ByteArray = ivField.get(encryptionHelper) as ByteArray

        encryptionHelper.saveMetaData(1)

        val methodForRetrieve = encryptionHelper.javaClass.getDeclaredMethod("retrieveMetaData", Int::class.java)
        methodForRetrieve.isAccessible = true
        methodForRetrieve.invoke(encryptionHelper, 1)

        val byteArrayAfterSave: ByteArray = ivField.get(encryptionHelper) as ByteArray

        Assert.assertTrue("Iv values are not equal",
            byteArrayBeforeSave.contentEquals(byteArrayAfterSave))

        val noteDec: NoteModel = encryptionHelper.decrypt(encText, 1)

        Assert.assertTrue(
            "Has error during decrypt: " + encryptionHelper.lastError,
            encryptionHelper.lastError == CryptoError.OK
        )
        Assert.assertTrue(
            "Failed to decrypt",
            noteDec.note == text && noteDec.title == title
        )

        Log.i(TAG, "test02_EncryptionHelper_decrypt_encrypt() OUT")
    }

}