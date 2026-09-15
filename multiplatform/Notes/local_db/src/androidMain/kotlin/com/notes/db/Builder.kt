package com.notes.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import api.Platform
import com.notes.db.impl.NoteDatabase
import com.notes.db.impl.NoteDatabase_Impl

private const val name = "appnotes.db"

private fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<NoteDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(name)
    val builder = Room.databaseBuilder<NoteDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
        factory = { NoteDatabase_Impl() }
    )
    builder.setDriver(BundledSQLiteDriver())
    return builder
}

fun getDatabaseInstance(context: Context): AppDatabase {
    Platform().logger.logi("getDatabaseInstance()")
    return AppDatabase(getDatabaseBuilder(context))
}
