package com.notes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.concurrent.AtomicBoolean
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import com.notes.api.PlatformAPIs.logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

@Database(entities = [NoteEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

interface DBLifecycleCallback {
    fun onCreate()
    fun onOpen()
    fun onClose()
}

@OptIn(ExperimentalCoroutinesApi::class)
object LocalNoteDatabase {

    private var noteConnection: SQLiteConnection? = null

    private var noteDb: NoteDatabase? = null

    private val isCreated = AtomicBoolean(false)

    private val localCallback: DBLifecycleCallback = object : DBLifecycleCallback {

        override fun onCreate() {
            logger.logi("DBLifecycleCallback: onCreate()")
            clientCallback?.onCreate()
        }

        override fun onClose() {
            logger.logi("DBLifecycleCallback: onClose()")
            clientCallback?.onClose()
        }

        override fun onOpen() {
            logger.logi("DBLifecycleCallback: onOpen()")
            clientCallback?.onOpen()
        }
    }

    private var clientCallback: DBLifecycleCallback? = null

    fun initialize(context: Context? = null, callback: DBLifecycleCallback? = null): NoteDatabase? {
        logger.logi("LocalDatabase: initialize()")
        if (isCreated.compareAndSet(false, true)) {
            clientCallback = callback
            val builder = Room.databaseBuilder<NoteDatabase>(context!!, "note_local_database")
            builder.addCallback(object : RoomDatabase.Callback() {

                override fun onCreate(db: SupportSQLiteDatabase) {
                    logger.logi("LocalDatabase: Callback.onCreate()")
                    super.onCreate(db)
                    localCallback.onCreate()
                }

                override fun onCreate(connection: SQLiteConnection) {
                    super.onCreate(connection)
                    noteConnection = connection
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    logger.logi("LocalDatabase: Callback.onOpen()")
                    super.onOpen(db)
                    localCallback.onOpen()
                }

            })
            noteDb = builder.build()
            logger.logi("LocalDatabase: initialize(): done")
            return noteDb
        } else {
            logger.logi("LocalDatabase: initialize(): no-op")
            return noteDb
        }
    }

    suspend fun access(): NoteDao {
        while (noteDb == null) {
            logger.logi("LocalDatabase: access() not initialized, waiting...")
            delay(100)
        }
        logger.logi("LocalDatabase: access() done")
        return noteDb!!.noteDao()
    }

    fun close() {
        if (noteConnection != null) {
            noteConnection?.close()
            noteConnection = null
            logger.logi("LocalDatabase: close(): connection is closed")
        } else {
            logger.logi("LocalDatabase: close(): connection is invalid")
        }

        noteDb?.close()
        noteDb = null
        isCreated.set(false)

        localCallback.onClose()
        clientCallback = null

        logger.logi("LocalDatabase: close(): done")
    }

}