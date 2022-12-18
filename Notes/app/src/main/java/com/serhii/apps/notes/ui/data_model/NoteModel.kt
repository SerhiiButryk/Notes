/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.data_model

import android.content.Context
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONObject

data class NoteModel(val note: String, val title: String, var time: String = "", val id: String = "") {

    val isEmpty: Boolean
        get() = note.isEmpty() && title.isEmpty()

    // TODO: Need to remove template notes handling
    fun isTemplate(context: Context): Boolean {
        return false
    }

    companion object {

        private const val NOTE_KEY = "NOTE_KEY"
        private const val TITLE_KEY = "TITLE_KEY"
        private const val TIME_KEY = "TIME_KEY"
        private const val ID_KEY = "ID_KEY"

        fun fromJson(json: String): NoteModel {
            val jsonObject = JSONObject(json)
            val note: String = jsonObject.get(NOTE_KEY).toString()
            val title: String = jsonObject.get(TITLE_KEY).toString()
            val time: String = jsonObject.get(TIME_KEY).toString()
            val id: String = jsonObject.get(ID_KEY).toString()
            return NoteModel(note, title, time, id)
        }

        fun getJson(note: NoteModel): String {
            val json = buildJsonObject {
                put(NOTE_KEY, note.note)
                put(TITLE_KEY, note.title)
                put(TIME_KEY, note.time)
                put(ID_KEY, note.id)
            }
            return json.toString()
        }

    }

    override fun toString(): String {
        return ""
    }
}