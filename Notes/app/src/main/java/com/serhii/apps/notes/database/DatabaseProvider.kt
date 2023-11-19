/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database

import android.content.Context

interface DatabaseProvider<T> {
    val recordsCount: Int
    fun init(context: Context)
    fun clear()
    fun addRecord(data: T, context: Context): Int
    fun deleteRecord(id: String): Boolean
    fun updateRecord(id: String, data: T, context: Context): Boolean
    fun getRecord(id: String, context: Context): T
    fun getRecords(context: Context): List<T>
    fun close()
}