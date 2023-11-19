/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database.impl

import android.content.Context
import android.util.Base64
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.data_model.NoteModel.Companion.fromJson
import com.serhii.apps.notes.ui.data_model.NoteModel.Companion.getJson
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.Cipher
import com.serhii.core.security.impl.crypto.CryptoError

class EncryptionHelper {

    private var ivNote: ByteArray? = null
    private var lastError = CryptoError.OK

    fun encrypt(noteModel: NoteModel): String {
        info(TAG, "encrypt() in")
        resetErrors()
        val csk = Cipher()
        val json = getJson(noteModel)
        val (message, iv) = csk.encryptSymmetric(json)
        ivNote = iv
        return message
    }

    fun decrypt(note: String, index: Int, context: Context): NoteModel {
        info(TAG, "decrypt() with index = $index, in")
        resetErrors()
        retrieveMetaData(context, index)
        return decryptInternal(note)
    }

    fun saveMetaData(context: Context, id: Int) {
        info(TAG, "saveMetaData() index $id, in")
        val fileName = IV_DATA_FILE + id

        // Save to shared preferences
        val preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val edit = preferences.edit()

        // Remove old metadata
        if (preferences.contains(KEY_IV_NOTE)) {
            edit.remove(KEY_IV_NOTE)
        }

        edit.putString(KEY_IV_NOTE, String(Base64.encode(ivNote, Base64.NO_WRAP)))
        edit.apply()

        info(TAG, "saveMetaData() out")
    }

    private fun retrieveMetaData(context: Context, id: Int) {
        info(TAG, "retrieveMetaData() index $id")
        val fileName = IV_DATA_FILE + id

        // Retrieve from shared preferences
        val preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val id_ = preferences.getString(KEY_IV_NOTE, "")

        ivNote = Base64.decode(id_, Base64.NO_WRAP)
    }

    fun decrypt(data: Map<Int, String>, context: Context): List<NoteModel> {
        resetErrors()
        val noteDec: MutableList<NoteModel> = ArrayList()
        for ((key, value) in data) {
            info(TAG, "decryptData() index $key")
            val noteModel = decrypt(value, key, context)
            noteDec.add(noteModel)
        }
        return noteDec
    }

    private fun resetErrors() {
        lastError = CryptoError.OK
    }

    private fun decryptInternal(note: String): NoteModel {
        val csk = Cipher()

        if (ivNote != null) {
            val (json) = csk.decryptSymmetric(note, inputIV = ivNote!!)
            return fromJson(json)
        }

        Log.error(TAG, "decryptInternal() ivNote == null")
        return NoteModel()
    }

    companion object {
        private const val TAG = "EncryptionHelper"
        private const val IV_DATA_FILE = "com.example.app.db.pref.local"
        private const val KEY_IV_NOTE = "note_iv"
    }
}