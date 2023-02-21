/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database

import android.content.Context

interface NotesDatabase<T> {
    fun init(context: Context)
    fun clear()
    fun addRecord(uiData: T, context: Context): Int
    fun deleteRecord(id: String): Boolean
    fun updateRecord(id: String, uiData: T, context: Context): Boolean
    fun getRecord(id: String, context: Context): T
    fun getRecords(context: Context): List<T>
    val recordsCount: Int
    fun close()
}