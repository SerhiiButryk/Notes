/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database.impl

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import com.serhii.apps.notes.database.impl.database.DatabaseImpl
import com.serhii.apps.notes.database.impl.tables.NoteTableModel.QUERY_DROP_USER_NOTES_TABLE
import com.serhii.apps.notes.database.impl.tables.NoteTableModel.UserNotesEntry
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info

/**
 * Database impl for user notes
 */
object NotesDatabaseIml : DatabaseImpl() {

    private const val TAG = "NoteDatabaseIml"

    private var databaseWrite: SQLiteDatabase? = null
    private var databaseRead: SQLiteDatabase? = null

    override fun initDbImpl(context: Context) {
        if (!initialized) {
            info(TAG, "initDbImpl() init database")
            val noteDBHelper = NotesDBHelper(context)
            databaseWrite = noteDBHelper.writableDatabase
            databaseRead = noteDBHelper.readableDatabase
            initialized = true
        } else {
            info(TAG, "initDbImpl() already initialized")
        }
    }

    override fun clearDatabaseImpl() {
        super.clearDatabaseImpl()
        databaseWrite!!.execSQL(QUERY_DROP_USER_NOTES_TABLE)
    }

    override fun addRecordImpl(data: String): Int {
        super.addRecordImpl(data)
        info(TAG, "addRecordImpl()")

        val values = ContentValues()
        values.put(UserNotesEntry.COLUMN_NAME_NOTE, data)

        val row = databaseWrite!!.insert(UserNotesEntry.TABLE_NAME, null, values)
        if (row == -1L) {
            error(TAG, "addRecordImpl(), failed to insert values to database")
        } else {
            info(TAG, "addRecordImpl(), inserted, row = $row")
        }

        return row.toInt()
    }

    override fun deleteRecordImpl(id: String): Boolean {
        super.deleteRecordImpl(id)

        info(TAG, "deleteRecordImpl(), id=$id")

        val selection: String = UserNotesEntry._ID + FLAG_SELECT
        val selectionArgs = arrayOf(id)
        val count = databaseWrite!!.delete(UserNotesEntry.TABLE_NAME, selection, selectionArgs)

        info(TAG, "deleteRecordImpl(), deleted rows=$count")

        return count > 0
    }

    override fun updateRecordImpl(id: String, newData: String): Boolean {
        super.updateRecordImpl(id, newData)

        info(TAG, "updateRecordImpl() id=$id")

        val values = ContentValues()
        values.put(UserNotesEntry.COLUMN_NAME_NOTE, newData)

        val count = databaseWrite!!.update(UserNotesEntry.TABLE_NAME, values, UserNotesEntry._ID + FLAG_SELECT, arrayOf<String>(id))

        if (count == 0) {
            error(TAG, "updateRecordImpl(), no rows updated")
        }

        return count != 0
    }

    override fun getRecordImpl(id: String): String {
        super.getRecordImpl(id)
        return getRecord(id)
    }

    @get:SuppressLint("Range", "Recycle")
    override val recordsImpl: Map<Int, String>
        get() {
            super.recordsImpl

            val data: MutableMap<Int, String> = HashMap()

            val c = databaseRead!!.rawQuery("SELECT * FROM " + UserNotesEntry.TABLE_NAME, null)

            while (c.moveToNext()) {
                val id = c.getString(c.getColumnIndex(UserNotesEntry._ID))
                val note = c.getString(c.getColumnIndex(UserNotesEntry.COLUMN_NAME_NOTE))
                data[id.toInt()] = note
            }

            return data
        }

    override fun closeImpl() {
        super.closeImpl()

        databaseWrite?.close()
        databaseRead?.close()

        databaseRead = null
        databaseWrite = null
        initialized = false
    }

    override val recordsCountImpl: Int
        get() {
            val count = DatabaseUtils.queryNumEntries(databaseRead, UserNotesEntry.TABLE_NAME)
            info(TAG, "getRecordsCount(), count = $count")
            return count.toInt()
        }

    @SuppressLint("Range")
    private fun getRecord(id: String): String {
        info(TAG, "getRecord() id=$id")

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf<String>(
            UserNotesEntry._ID,
            UserNotesEntry.COLUMN_NAME_NOTE
        )

        // Filter results WHERE "title" = 'My Title'
        val selection: String = UserNotesEntry._ID + FLAG_SELECT
        val selectionArgs = arrayOf(id)

        val c = databaseRead!!.query(
            UserNotesEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (c.count == 0) {
            error(TAG, "getRecord(), no records found")
        } else {
            info(TAG, "getRecord(), row count:" + c.count)
        }

        var data = ""

        if (c.moveToNext()) {
            data = c.getString(c.getColumnIndex(UserNotesEntry.COLUMN_NAME_NOTE))
        }
        c.close()

        return data
    }
}