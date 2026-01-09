package com.notes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.concurrent.AtomicBoolean
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import api.PlatformAPIs.logger
import kotlinx.coroutines.delay

@Database(entities = [NoteEntity::class, NotesMetadataEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun noteMetadataDao(): NoteMetadataDao
}

object LocalNoteDatabase : LocalNoteDatabaseImpl() {

    suspend fun testOnly_access(): NoteDao = accessInternal().noteDao()

    suspend fun access(): NoteDao = EncryptedNoteDao(accessInternal().noteDao())

    suspend fun accessNoteMetadata(): NoteMetadataDao = accessInternal().noteMetadataDao()
}

interface DBLifecycleCallback {
    fun onCreate()

    fun onOpen()

    fun onClose()
}

abstract class LocalNoteDatabaseImpl {

    private var noteConnection: SQLiteConnection? = null
    private var noteDb: NoteDatabase? = null
    private val isCreated = AtomicBoolean(false)
    private var clientCallback: DBLifecycleCallback? = null

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

    fun initialize(
        context: Context? = null,
        callback: DBLifecycleCallback? = null,
    ): NoteDatabase? {
        logger.logi("LocalDatabase: initialize()")
        if (isCreated.compareAndSet(false, true)) {
            clientCallback = callback
            val builder = Room.databaseBuilder<NoteDatabase>(context!!, "note_local_database")
            builder.addCallback(
                object : RoomDatabase.Callback() {
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
                },
            )
            noteDb = builder.build()
            logger.logi("LocalDatabase: initialize(): done")
            return noteDb
        } else {
            logger.logi("LocalDatabase: initialize(): no-op")
            return noteDb
        }
    }

    suspend fun accessInternal(): NoteDatabase {
        while (noteDb == null) {
            logger.logi("LocalDatabase: accessInternal() not initialized, waiting...")
            delay(100)
        }
        logger.logi("LocalDatabase: accessInternal() done")
        return noteDb!!
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
