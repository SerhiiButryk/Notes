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
import com.serhii.apps.notes.ui.utils.NoteEditorAdapter
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AppTests {

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

    @Test
    fun test02_NoteModel_empty_data() {
        Log.i(TAG, "test02_NoteModel_empty_data() IN")

        val adapter = NoteEditorAdapter()

        val noteModel = adapter.getNote()
        Assert.assertTrue("Note is not empty", noteModel.isEmpty)

        Assert.assertTrue("Wrong", noteModel.note.isEmpty())
        Assert.assertTrue("Wrong", noteModel.title.isEmpty())
        Assert.assertTrue("Wrong", noteModel.id.isEmpty())
        Assert.assertTrue("Wrong", noteModel.time.isEmpty())
        Assert.assertTrue("Wrong", noteModel.listNote.isEmpty())
        Assert.assertTrue("Wrong", noteModel.viewType == NoteModel.ONE_NOTE_VIEW_TYPE)

        Log.i(TAG, "test02_NoteModel_empty_data() OUT")
    }


    @Test
    fun test03_AdapterNote_empty_data() {
        Log.i(TAG, "test03_AdapterNote_empty_data() IN")

        val adapter = NoteEditorAdapter()

        val noteModel = adapter.getNote()
        Assert.assertTrue("Note is not empty", noteModel.isEmpty)

        val noteList = adapter.getNoteList();
        Assert.assertTrue("List is not empty", noteList.isEmpty())

        Log.i(TAG, "test03_AdapterNote_empty_data() OUT")
    }

    @Test
    fun test04_AdapterNote_test_list_data() {
        Log.i(TAG, "test04_AdapterNote_test_list_data() IN")

        val adapter = NoteEditorAdapter()

        val listText1 = "My note"
        val listText2 = "My note2"
        val listChecked1 = true
        val listChecked2 = false

        val listNote = mutableListOf(NoteList(listText1, listChecked1))
        val listNote2 = mutableListOf(NoteList(listText2, listChecked2))

        val noteText1 = "Note test"
        val noteTitle1 = "Title test"
        val noteTime1 = "time"
        val noteId = "1"

        val noteTest = NoteModel(noteText1, noteTitle1, noteTime1, noteId,
            NoteModel.LIST_NOTE_VIEW_TYPE, listNote)

        val noteTest2 = NoteModel(noteText1, noteTitle1, noteTime1, noteId,
            NoteModel.LIST_NOTE_VIEW_TYPE, listNote2)

        val testList = mutableListOf(noteTest, noteTest2)

        adapter.setDataChanged(mutableListOf(noteTest, noteTest2))

        var actualNote = adapter.getNote()

        Assert.assertTrue("Wrong note", actualNote.note == noteText1)
        Assert.assertTrue("Wrong title", actualNote.title == noteTitle1)
        Assert.assertTrue("Wrong id", actualNote.id == noteId)
        Assert.assertTrue("Wrong time", actualNote.time == noteTime1)
        Assert.assertTrue("Wrong view type", actualNote.viewType == NoteModel.LIST_NOTE_VIEW_TYPE)

        Assert.assertTrue("Wrong size", actualNote.listNote.size == 2)

        val note1 = actualNote.listNote[0]
        Assert.assertTrue("Wrong note 1", note1.note == listText1)
        Assert.assertTrue("Wrong check box 1", note1.isChecked == listChecked1)

        val note2 = actualNote.listNote[1]
        Assert.assertTrue("Wrong note 2", note2.note == listText2)
        Assert.assertTrue("Wrong check box 2", note2.isChecked == listChecked2)

        var actualList = adapter.getNoteList()
        Assert.assertTrue("Wrong list", actualList == testList)

        adapter.transformView()

        /**
         * Check note after transformation
         */

        actualNote = adapter.getNote()

        Assert.assertTrue("Wrong note", actualNote.note == noteText1)
        Assert.assertTrue("Wrong title", actualNote.title == noteTitle1)
        Assert.assertTrue("Wrong id", actualNote.id == noteId)
        Assert.assertTrue("Wrong time", actualNote.time == noteTime1)
        Assert.assertTrue("Wrong viewType", actualNote.viewType == NoteModel.ONE_NOTE_VIEW_TYPE)

        Assert.assertTrue("Wrong size", actualNote.listNote.size == 2)

        actualList = adapter.getNoteList()
        Assert.assertTrue("Wrong list", actualList == testList)

        Log.i(TAG, "test04_AdapterNote_test_list_data() OUT")
    }

    companion object {
        private val TAG: String = AppTests::class.java.simpleName
        private const val userName: String = "myUser"
        private const val userPassword: String = "myPassword"

        var scenario: ActivityScenario<NotesViewActivity>? = null

        @BeforeClass
        @JvmStatic
        fun setup() {
            Log.i(TAG, "setup() IN")
            scenario = TestUtility.launchApp(userName, userPassword)
            Log.i(TAG, "setup() OUT")
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            Log.i(TAG, "teardown() IN")
            scenario?.let {
                TestUtility.closeApp(it)
            }
            scenario = null
            Log.i(TAG, "teardown() OUT")
        }
    }

}