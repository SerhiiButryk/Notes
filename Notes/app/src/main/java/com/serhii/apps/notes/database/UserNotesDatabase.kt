/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database

import android.content.Context
import com.serhii.apps.notes.database.impl.NotesDatabaseIml
import com.serhii.apps.notes.database.impl.database.DatabaseImpl
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.Crypto
import com.serhii.core.utils.GoodUtils.Companion.currentTimeToString

/**
 * This is an access point into application notes database.
 * This class adds an encryption layer into the database.
 */
object UserNotesDatabase : DatabaseProvider<NoteModel> {

    private const val TAG = "UserNotesDatabase"

    private val impl: DatabaseImpl
    private val crypto = Crypto()

    init {
        // Set an implementation class
        impl = NotesDatabaseIml
    }

    override fun init(context: Context) {
        impl.initDbImpl(context)
    }

    override fun clear() {
        impl.clearDatabaseImpl()
    }

    override fun addRecord(data: NoteModel): Int {
        // Save the saving time for note
        data.time = currentTimeToString()

        // Create and insert empty string before real data
        // This is a workaround to know row id beforehand
        val index = impl.addRecordImpl("")
        if (index == -1) {
            error(TAG, "addRecord(), failed to add empty record, returned index -1")
            return -1
        }

        val rowId = index.toString()
        data.id = rowId

        val noteEnc = encrypt(data)

        impl.updateRecordImpl(rowId, noteEnc)

        info(TAG, "addRecord(), added new note with index = $index")

        return index
    }

    override fun deleteRecord(id: String): Boolean {
        val success = impl.deleteRecordImpl(id)
        info(TAG, "deleteRecord(), id = $id, result = $success")
        return success
    }

    override fun updateRecord(id: String, data: NoteModel): Boolean {
        // Save the saving time for note
        data.time = currentTimeToString()
        data.id = id

        val noteEnc = encrypt(data)

        return impl.updateRecordImpl(id, noteEnc)
    }

    override fun getRecord(id: String): NoteModel {
        val data = impl.getRecordImpl(id)
        return decrypt(data)
    }

    override fun getRecords(): List<NoteModel> {
        val data = impl.records
        Log.detail(TAG, "getRecords() size = ${data.size}")
        return decrypt(data)
    }

    override val recordsCount: Int
        get() = impl.recordsCount

    override fun close() {
        impl.closeImpl()
    }

    private fun encrypt(noteModel: NoteModel): String {
        Log.detail(TAG, "encrypt()")
        val json = NoteModel.getJson(noteModel)
        return crypto.encrypt(json)
    }

    private fun decrypt(note: String): NoteModel {
        Log.detail(TAG, "decrypt()")
        val json = crypto.decrypt(note)
        return NoteModel.fromJson(json)
    }

    private fun decrypt(data: Map<Int, String>): List<NoteModel> {
        val noteDec: MutableList<NoteModel> = ArrayList()
        for ((key, value) in data) {
            Log.detail(TAG, "decrypt() index $key")
            val noteModel = decrypt(value)
            noteDec.add(noteModel)
        }
        return noteDec
    }
}