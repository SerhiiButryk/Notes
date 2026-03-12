package com.notes.db.impl

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import api.Platform
import com.notes.db.AppDatabase
import java.io.File

private const val name = "appnotes.db"

private fun getDatabaseBuilder(): RoomDatabase.Builder<NoteDatabase> {
    Platform().logger.logi("getDatabaseBuilder()")
    val dbFile = File(Platform().getCacheDir(), name)
    val builder = Room.databaseBuilder<NoteDatabase>(
        name = dbFile.absolutePath,
        factory = { NoteDatabase_Impl() },
    )
    builder.setDriver(BundledSQLiteDriver())
    return builder
}

fun getDatabaseInstance(): AppDatabase {
    Platform().logger.logi("getDatabaseInstance()")
    return AppDatabase(getDatabaseBuilder())
}
