/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.data_model

import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONArray
import org.json.JSONObject

data class NoteList(var note: String = "", var isChecked:Boolean = false)

data class NoteModel(var note: String = "", val title: String = "",
                     var time: String = "", var id: String = "",
                     var viewType: Int = ONE_NOTE_VIEW_TYPE,
                     /**
                      * Currently there can be only 1 item in list
                      */
                     var listNote: MutableList<NoteList> = mutableListOf()) {

    val isEmpty: Boolean
        get() = note.isEmpty() && title.isEmpty()

    /**
     * Currently there can be only 1 item in list
     */
    fun getNoteList(): NoteList {
        if (listNote.isEmpty())
            return NoteList()
        return listNote[0]
    }

    /**
     * Currently there can be only 1 item in list
     */
    fun putListNote(note: String) {
        if (listNote.isEmpty()) {
            listNote.add(NoteList(note))
        } else {
            listNote[0].note = note
        }
    }

    /**
     * Currently there can be only 1 item in list
     */
    fun putListNoteChecked(isChecked: Boolean) {
        if (listNote.isNotEmpty()) {
            listNote[0].isChecked = isChecked
        }
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

        fun getJson(note: NoteModel): String {

            val jsonArray = buildJsonArray {
                for (n in note.listNote) {
                    addJsonObject {
                        put(NOTE_KEY, n.note)
                        put(IS_CHECKED_KEY, n.isChecked)
                    }
                }
            }

            val json = buildJsonObject {
                put(NOTE_KEY, note.note)
                put(TITLE_KEY, note.title)
                put(TIME_KEY, note.time)
                put(ID_KEY, note.id)
                put(TYPE_KEY, note.viewType)
                put(NOTE_LIST_KEY, jsonArray)
            }

            return json.toString()
        }

        fun getCopy(note: NoteModel) = NoteModel(note.note, note.title, note.time, note.id,
            note.viewType)

        fun create(note: String, title: String, time: String, id: String): NoteModel {
            return NoteModel(note, title, time, id)
        }

        fun create(): NoteModel {
            return NoteModel()
        }

    }

    override fun toString(): String {
        return ""
    }
}