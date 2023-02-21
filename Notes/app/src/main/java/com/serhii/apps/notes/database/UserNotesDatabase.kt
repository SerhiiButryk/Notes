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
 * Singleton implementation for user notes database.
 * Also this class adds encryption layer for note data.
 */
object UserNotesDatabase : NotesDatabase<NoteModel> {

    private val impl: DatabaseImpl
    private const val TAG = "UserNotesDatabase"

    init {
        impl = NotesDatabaseIml
    }

    override fun init(context: Context) {
        impl.initDbImpl(context)
    }

    override fun clear() {
        impl.clearDatabaseImpl()
    }

    override fun addRecord(uiData: NoteModel, context: Context): Int {
        // Set last saved time for note
        uiData.time = currentTimeToString()

        // Create and insert empty string before real data
        // This is a workaround to know row id beforehand
        val index = impl.addRecordImpl("")
        if (index == -1) {
            error(TAG, "addRecord(), failed to add empty record, returned index -1")
            return -1
        }
        val rowId = index.toString()
        uiData.id = rowId

        val encryptionHelper = EncryptionHelper(context)

        val noteEnc = encryptionHelper.encrypt(uiData)
        val result = impl.updateRecordImpl(rowId, noteEnc)
        info(TAG, "addRecord(), added new note with index = $index")
        if (result) {
            encryptionHelper.saveMetaData(index)
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
        // Set last saved time for note
        newData.time = currentTimeToString()
        // Set note id
        newData.id = id
        val encryptionHelper = EncryptionHelper(context)
        val noteEnc = encryptionHelper.encrypt(newData)
        // Save meta data
        if (noteEnc != null && noteEnc.isNotEmpty()) {
            encryptionHelper.saveMetaData(id.toInt())
        }
        return impl.updateRecordImpl(id, noteEnc)
    }

    override fun getRecord(id: String, context: Context): NoteModel {
        val data = impl.getRecordImpl(id)
        val encryptionHelper = EncryptionHelper(context)
        return encryptionHelper.decrypt(data, Integer.valueOf(id))
    }

    override fun getRecords(context: Context): List<NoteModel> {
        val data = impl.recordsImpl
        val encryptionHelper = EncryptionHelper(context)
        return encryptionHelper.decrypt(data)
    }

    override val recordsCount: Int
        get() = impl.recordsCountImpl

    override fun close() {
        impl.closeImpl()
    }
}