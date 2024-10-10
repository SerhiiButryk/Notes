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
    fun addRecord(data: T): Int
    fun deleteRecord(id: String): Boolean
    fun updateRecord(id: String, data: T): Boolean
    fun getRecord(id: String): T
    fun getRecords(): List<T>
    fun close()
}