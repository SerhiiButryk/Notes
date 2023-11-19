/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database

import android.content.Context
import com.serhii.apps.notes.database.impl.EncryptionHelper
import com.serhii.apps.notes.database.impl.NotesDatabaseIml
import com.serhii.apps.notes.database.impl.database.DatabaseImpl
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.utils.GoodUtils.Companion.currentTimeToString

/**
 * This is a access point into User's notes database.
 * Also this class adds an encryption layer for data storage.
 */
object UserNotesDatabase : DatabaseProvider<NoteModel> {

    private val impl: DatabaseImpl
    private const val TAG = "UserNotesDatabase"
    private val encryptionHelper = EncryptionHelper()

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

    override fun addRecord(data: NoteModel, context: Context): Int {
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

        val noteEnc = encryptionHelper.encrypt(data)
        val result = impl.updateRecordImpl(rowId, noteEnc)

        info(TAG, "addRecord(), added new note with index = $index")

        if (result) {
            encryptionHelper.saveMetaData(context, index)
        } else {
            error(TAG, "addRecord(), failed to add new record, returned index -1")
        }

        return index
    }

    override fun deleteRecord(id: String): Boolean {
        val success = impl.deleteRecordImpl(id)
        info(TAG, "deleteRecord(), id = $id, result = $success")
        return success
    }

    override fun updateRecord(id: String, newData: NoteModel, context: Context): Boolean {
        // Save the saving time for note
        newData.time = currentTimeToString()
        newData.id = id

        val noteEnc = encryptionHelper.encrypt(newData)
        // Save meta data
        if (noteEnc.isNotEmpty()) {
            encryptionHelper.saveMetaData(context, id.toInt())
        }
        return impl.updateRecordImpl(id, noteEnc)
    }

    override fun getRecord(id: String, context: Context): NoteModel {
        val data = impl.getRecordImpl(id)
        return encryptionHelper.decrypt(data, Integer.valueOf(id), context)
    }

    override fun getRecords(context: Context): List<NoteModel> {
        val data = impl.records
        return encryptionHelper.decrypt(data, context)
    }

    override val recordsCount: Int
        get() = impl.recordsCount

    override fun close() {
        impl.closeImpl()
    }
}