/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.data_model

import com.serhii.apps.notes.ui.search.SearchableInfo
import com.serhii.core.log.Log
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONArray
import org.json.JSONObject

/**
 * Classes which represent a note in this application
 */

data class NoteList(var note: String = "", var isChecked:Boolean = false)

data class NoteModel(var note: String = "", var title: String = "",
                     var time: String = "", var id: String = "" /* Record id of note in the database */,
                     var viewType: Int = ONE_NOTE_VIEW_TYPE,
                     /**
                      * Currently there can be only 1 item in list
                      */
                     val listNote: MutableList<NoteList> = mutableListOf()) {

    var queryInfo: SearchableInfo? = null

    /*
    * If this note has no user notes
    */
    val isEmpty: Boolean
        get() {
            if (viewType == ONE_NOTE_VIEW_TYPE) {
                return note.isEmpty() && title.isEmpty()
            } else if (viewType == LIST_NOTE_VIEW_TYPE) {
                var isEmpty = true
                for (n in listNote) {
                    if (n.note.isNotEmpty() || title.isNotEmpty()) {
                        isEmpty = false
                    }
                }
                return isEmpty
            }
            return false
        }

    fun getNoteList(): NoteList {
        if (listNote.isEmpty())
            return NoteList()
        return listNote[0]
    }

    fun putListNote(note: String, isChecked: Boolean) {
        listNote.add(NoteList(note, isChecked))
    }

    fun clearNotes() {

        if (viewType == ONE_NOTE_VIEW_TYPE) {
            note = ""
        }

        if (viewType == LIST_NOTE_VIEW_TYPE) {
            for (n in listNote) {
                n.note = ""
                n.isChecked = false
            }
        }

    }

    override fun toString(): String {
        return ""
    }

    companion object {

        val EMPTY_NOTE = NoteModel()

        private const val NOTE_KEY = "NOTE_KEY"
        private const val TITLE_KEY = "TITLE_KEY"
        private const val TIME_KEY = "TIME_KEY"
        private const val ID_KEY = "ID_KEY"
        private const val TYPE_KEY = "TYPE_KEY"
        private const val NOTE_LIST_KEY = "NOTE_LIST_KEY"
        private const val IS_CHECKED_KEY = "IS_CHECKED_KEY"

        const val ONE_NOTE_VIEW_TYPE = 1
        const val LIST_NOTE_VIEW_TYPE = 2

        fun fromJson(json: String): NoteModel {

            val jsonObject = JSONObject(json)
            val note = jsonObject.get(NOTE_KEY).toString()
            val title = jsonObject.get(TITLE_KEY).toString()
            val time = jsonObject.get(TIME_KEY).toString()
            val id = jsonObject.get(ID_KEY).toString()
            val viewType = jsonObject.get(TYPE_KEY).toString()
            val arrayNoteList = jsonObject.get(NOTE_LIST_KEY).toString()

            val resultArray = mutableListOf<NoteList>()

            val arrayJson = JSONArray(arrayNoteList)
            if (arrayJson.length() > 0) {
                for (i in 0 until arrayJson.length()) {
                    val jsonObj = arrayJson.getJSONObject(i)
                    val noteText = jsonObj.getString(NOTE_KEY)
                    val isChecked = jsonObj.getBoolean(IS_CHECKED_KEY)
                    resultArray.add(NoteList(noteText, isChecked))
                }
            }

            return NoteModel(note, title, time, id, Integer.parseInt(viewType), resultArray)
        }

        fun convertNoteListToJson(notes: List<NoteModel>): String {

            val notesJsonObjects = mutableListOf<JsonObject>()

            for (note in notes) {
                val jsonObject = getJsonObject(note)
                notesJsonObjects.add(jsonObject)
            }

            val jsonArray = buildJsonArray {
                for (jsonObject in notesJsonObjects) {
                    add(jsonObject)
                }
            }

            return jsonArray.toString()
        }

        fun convertJsonToNoteList(json: String): List<NoteModel> {

            val list = mutableListOf<NoteModel>()

            try {
                val jsonArray = JSONArray(json)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.get(i)
                    val noteModel = fromJson(jsonObject.toString())
                    list.add(noteModel)
                }
            } catch (e: Exception) {
                Log.error("NoteModel", "convertJsonToNoteList() failed to convert, error = $e")
                e.printStackTrace()
                return emptyList()
            }

            return list
        }

        fun getJson(note: NoteModel): String {
            return getJsonObject(note).toString()
        }

        private fun getJsonObject(note: NoteModel): JsonObject {
            val noteListTypeArray = buildJsonArray {
                for (n in note.listNote) {
                    addJsonObject {
                        put(NOTE_KEY, n.note)
                        put(IS_CHECKED_KEY, n.isChecked)
                    }
                }
            }

            return buildJsonObject {
                put(NOTE_KEY, note.note)
                put(TITLE_KEY, note.title)
                put(TIME_KEY, note.time)
                put(ID_KEY, note.id)
                put(TYPE_KEY, note.viewType)
                put(NOTE_LIST_KEY, noteListTypeArray)
            }
        }

        fun getCopy(note: NoteModel): NoteModel {
            val copyNote = NoteModel(note.note, note.title, note.time, note.id, note.viewType, note.listNote)
            copyNote.queryInfo = note.queryInfo
            return copyNote
        }

        fun getCopy(note: NoteModel, listNote: MutableList<NoteList>): NoteModel {
            val copyNote = NoteModel(note.note, note.title, note.time, note.id,
                note.viewType, listNote)
            copyNote.queryInfo = note.queryInfo
            return copyNote
        }

        fun create(note: String, title: String, time: String, id: String): NoteModel {
            return NoteModel(note, title, time, id)
        }

        fun create(): NoteModel {
            return NoteModel()
        }

    }
}