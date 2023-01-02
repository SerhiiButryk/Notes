/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes

import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.apps.notes.activities.NotesViewActivity
import com.serhii.apps.notes.ui.data_model.NoteList
import com.serhii.apps.notes.ui.data_model.NoteModel
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AppTests {

    var scenario: ActivityScenario<NotesViewActivity>? = null

    @Before
    fun setup() {
        Log.i(TAG, "setup() IN")

        scenario = TestUtility.launchApp(userName, userPassword)

        Log.i(TAG, "setup() OUT")
    }

    @Test
    fun test01_Simple_Note_serialize_deserialize() {
        Log.i(TAG, "test01_Simple_Note_serialize_deserialize() IN")

        val noteText = "hello note"
        val noteTitle = "hello title"
        val type = NoteModel.LIST_NOTE_VIEW_TYPE
        val id = "1"
        val time = "22.22.22"

        val noteText2 = "my note2"
        val isChecked = true

        val listNote = mutableListOf(NoteList(noteText2, isChecked))

        val note = NoteModel(noteText, noteTitle, time, id, type, listNote)

        val json = NoteModel.getJson(note)

        Assert.assertNotNull("Failed to get json", json)
        Assert.assertTrue("Json is empty", json.isNotEmpty())

        val note2 = NoteModel.fromJson(json)

        Assert.assertNotNull("Note is null", note2)

        Assert.assertTrue("Wrong note", note2.note == noteText)
        Assert.assertTrue("Wrong title", note2.title == noteTitle)
        Assert.assertTrue("Wrong id", note2.id == id)
        Assert.assertTrue("Wrong time", note2.time == time)
        Assert.assertTrue("Wrong type", note2.viewType == type)
        Assert.assertTrue("Wrong list note", note2.listNote.isNotEmpty())
        Assert.assertTrue("Wrong list note", note2.listNote.size == 1)

        for (n in note2.listNote) {
            Assert.assertTrue("Wrong note 2", n.note == noteText2)
            Assert.assertTrue("Wrong bool flag", n.isChecked == isChecked)
        }

        Log.i(TAG, "test01_Simple_Note_serialize_deserialize() OUT")
    }

    @After
    fun teardown() {
        Log.i(TAG, "teardown() IN")
        scenario?.let {
            TestUtility.closeApp(it)
        }
        Log.i(TAG, "teardown() OUT")
    }

    @Test
    fun test() {}

    companion object {
        private val TAG: String = AppTests::class.java.simpleName
        private val userName: String = "myUser"
        private val userPassword: String = "myPassword"
    }

}