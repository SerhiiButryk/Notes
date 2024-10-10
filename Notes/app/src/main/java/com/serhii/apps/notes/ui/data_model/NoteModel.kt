/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.data_model

import com.serhii.core.log.Log
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONArray
import org.json.JSONObject

/**
 * Classes which represent a note in this application
 */

data class NoteModel(var plainText: String = "", var id: String = "" /* Record id of note in the database */) {

    val isEmpty: Boolean
        get() = plainText.isEmpty()

    fun clear() {
        plainText = ""
    }

    override fun toString(): String {
        return ""
    }

    companion object {

        private const val NOTE_KEY = "NOTE_KEY"
        private const val ID_KEY = "ID_KEY"

        fun fromJson(json: String): NoteModel {

            val jsonObject = JSONObject(json)
            val note = jsonObject.get(NOTE_KEY).toString()
            val id = jsonObject.get(ID_KEY).toString()

            return NoteModel(note, id)
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
            return buildJsonObject {
                put(NOTE_KEY, note.plainText)
                put(ID_KEY, note.id)
            }
        }

        fun create(note: String, id: String): NoteModel {
            return NoteModel(note, id)
        }

        fun create(): NoteModel {
            return NoteModel()
        }

    }
}