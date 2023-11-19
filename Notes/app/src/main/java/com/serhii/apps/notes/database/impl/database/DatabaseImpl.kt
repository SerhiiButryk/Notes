/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database.impl.database

import android.content.Context

abstract class DatabaseImpl {

    protected var initialized = false

    open fun initDbImpl(context: Context) {}

    open fun clearDatabaseImpl() {
        checkIfInitialized()
    }

    open fun addRecordImpl(data: String): Int {
        checkIfInitialized()
        return -1
    }

    open fun deleteRecordImpl(id: String): Boolean {
        checkIfInitialized()
        return false
    }

    open fun updateRecordImpl(id: String, newData: String): Boolean {
        checkIfInitialized()
        return false
    }

    open fun getRecordImpl(id: String): String {
        checkIfInitialized()
        return ""
    }

    open val records: Map<Int, String>
        get() {
            checkIfInitialized()
            return emptyMap()
        }

    open fun closeImpl() {
        checkIfInitialized()
    }

    open val recordsCount: Int
        get() {
            checkIfInitialized()
            return 0
        }

    private fun checkIfInitialized() {
        check(initialized) { "Database was not initialized" }
    }

    companion object {
        const val FLAG_SELECT = "=?"
        const val FLAG_ORDER = " ASC"
    }
}