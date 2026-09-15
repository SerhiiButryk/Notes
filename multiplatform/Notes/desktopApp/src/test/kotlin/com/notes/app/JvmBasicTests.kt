package com.notes.app

import api.AppService
import api.Platform
import api.data.AbstractStorageService
import api.data.Document
import api.data.Notes
import com.google.common.truth.Truth.assertThat
import com.notes.db.impl.getDatabaseInstance
import com.notes.db.model.NoteMetadata
import com.notes.os.impl.crypto.JVMKeyStore
import com.notes.repo.FilesManager
import com.notes.repo.JvmSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test

class JvmBasicTests : BaseTest() {

    @Test
    fun test01_keystore_encrypt_decrypt() =
        runTest {

            val pass = "hasdsdwowdkwdwodk12718"
            val cryptoKeyStore = JVMKeyStore(pass.toCharArray())
            cryptoKeyStore.init(mustCreateKeyStore = true)

            assertThat(cryptoKeyStore.testOnly_getKeystoreFile().exists()).isTrue()

            val text = "hello"
            val cipherText = cryptoKeyStore.encrypt(text)

            val plainText = cryptoKeyStore.decrypt(cipherText)

            assertThat(plainText).isEqualTo(text)

            cryptoKeyStore.onDestroy()
        }

    @Test
    fun test02_crypto_encrypt_decrypt() =
        runTest {

            val crypto = Platform().crypto

            val pass = "hasdsdwowdkwdwodk12718"
            val email = "eywfyu@gmail"

            crypto.onAuthCompleted(password = pass, uid = email)

            val text =
                """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. 
                Lorem Ipsum has been the industry's standard dummy text ever since 1966, when 
                designers at Letraset and James Mosley, the librarian at St Bride Printing Library, 
                took a 1914 Cicero translation and scrambled it to make dummy text for Letraset's 
                Body Type sheets. It has survived not only many decades, but also the leap into 
                electronic typesetting, remaining essentially unchanged. It was popularised thanks 
                to these sheets and more recently with desktop publishing software including versions 
                of Lorem Ipsum.
                """.trimIndent()

            val cipherText = crypto.encrypt(text)

            val plainText = crypto.decrypt(cipherText)

            assertThat(plainText).isEqualTo(text)

            crypto.onDestroy()
        }

    @Test
    fun test03_data_store_sanity_check() =
        runTest {

            val result = Platform().storage.save(value = "hello", key = "app")

            assertThat(result).isTrue()

            val str = Platform().storage.get(key = "app")

            assertThat(str).isEqualTo("hello")

            Platform().storage.clearAll()

            val str2 = Platform().storage.get(key = "app")

            assertThat(str2).isEmpty()
        }

    @Test
    fun test04_db_sanity_check() =

        runTest {

            val db = getDatabaseInstance()

            val records = db.fetch()
            assertThat(records.isEmpty()).isTrue()

            val record = db.select(1)
            assertThat(record).isNull()

            val metadata = "hello"
            db.insert(NoteMetadata(metadata = metadata))

            val recordsAfter = db.fetch()
            assertThat(recordsAfter.isEmpty()).isFalse()
            assertThat(recordsAfter.size == 1).isTrue()

            val actual = recordsAfter[0]

            assertThat(actual.id != 0L).isTrue()
            assertThat(actual.metadata == metadata).isTrue()
            assertThat(actual.noteId).isNull()
            assertThat(actual.pendingDelete).isFalse()

            val recordAfter = db.select(actual.id)
            assertThat(recordAfter).isNotNull()

            assertThat(recordAfter!!.id != 0L).isTrue()
            assertThat(recordAfter.metadata == metadata).isTrue()
            assertThat(recordAfter.noteId).isNull()
            assertThat(recordAfter.pendingDelete).isFalse()

            db.delete(actual.id)

            val recordsAfterDeletion = db.fetch()
            assertThat(recordsAfterDeletion.isEmpty()).isTrue()

            db.close()
        }

    @Test
    fun test05_sync_manager_sanity_check() =

        runTest {

            val syncManager = JvmSyncManager()

            val fileManager = FilesManager()
            val dir = File(fileManager.secondCacheDir)
            assertThat(dir.isDirectory).isTrue()
            assertThat(dir.list().size == 0).isTrue()

            // -------- Start -------------------------------

            val notesChannel = Channel<List<Notes>>()

            backgroundScope.launch(Dispatchers.IO) {
                syncManager.notes.collect {
                    Platform().logger.logi("test05_sync_manager_sanity_check: new notes received $it")
                    notesChannel.send(it)
                }
            }

            assertThat(syncManager.isAllInSync()).isTrue()

            val first = Notes(id = 1, content = "content 1")
            val second = Notes(id = 2, content = "content 2")

            val noteList = listOf(
                first,
                second,
            )

            syncManager.store(noteList, false, this)

            // Waiting is required for some short time,
            // as we don't always get new data immediately
            var result: ChannelResult<List<Notes>>? = null
            for (i in 0..10) {
                Platform().logger.logi("test05_sync_manager_sanity_check: waiting...")
                val res = notesChannel.tryReceive()
                if (res.isSuccess && res.getOrNull()!!.isNotEmpty()) {
                    result = res
                    break
                }
                Thread.sleep(1_000)
            }

            Platform().logger.logi("test05_sync_manager_sanity_check: continue")

            val data = result!!.getOrNull()
            assertThat(data != null).isTrue()

            assertThat(data!!.isNotEmpty()).isTrue()
            assertThat(data.size == 2).isTrue()

            // Insert fake metadata to test correct deletion

            val metadataFirst = NoteMetadata(noteId = first.id, pendingDelete = true)
            syncManager.__getDatabase_FOR_TEST().insert(metadataFirst)

            val metadataSecond = NoteMetadata(noteId = second.id, pendingDelete = true)
            syncManager.__getDatabase_FOR_TEST().insert(metadataSecond)

            syncManager.delete(first)
            syncManager.delete(second)

            // -------- End -------------------------------

            val records = syncManager.__getDatabase_FOR_TEST().fetch()
            assertThat(records.isEmpty()).isTrue()

            val dirAfter = File(fileManager.secondCacheDir)
            assertThat(dirAfter.isDirectory).isTrue()
            assertThat(dirAfter.list().size == 0).isTrue()
        }

    @Test
    fun test06_clear_local_storage() =

        runTest {

            val syncManager = JvmSyncManager()

            // Make sure directory and database are empty

            val fileManager = FilesManager()
            val dir = File(fileManager.secondCacheDir)
            assertThat(dir.isDirectory).isTrue()
            assertThat(dir.list().size == 0).isTrue()

            assertThat(syncManager.__getDatabase_FOR_TEST().fetch().isEmpty()).isTrue()

            // Perform testing

            val note = Notes(content = "Test1", id = 1)

            syncManager.store(notes = listOf(note), false, this)

            assertThat(dir.list().size == 1).isTrue()

            syncManager.updateMetadata(dataStore = FakeDataStore(), note = note)

            assertThat(syncManager.isAllInSync()).isTrue()

            val records = syncManager.__getDatabase_FOR_TEST().fetch()
            assertThat(records.isEmpty()).isFalse()
            assertThat(records.size == 1).isTrue()

            // Clean up

            syncManager.clearLocalStorage()

            // Check actual result

            val recordsAfter = syncManager.__getDatabase_FOR_TEST().fetch()
            assertThat(recordsAfter.isEmpty()).isTrue()
            assertThat(recordsAfter.size == 0).isTrue()

            assertThat(dir.list().size == 0).isTrue()

        }

    private class FakeDataStore : AbstractStorageService() {

        override val key: Any
            get() = AppService.FIREBASE_STORAGE

        override var canUse: Boolean = true

        override suspend fun store(document: Document): Boolean {
            return true
        }

        override suspend fun load(document: Document): Document? {
            return null
        }

        override suspend fun delete(document: Document): Boolean {
            return true
        }

        override suspend fun selectDocName(initial: Long): Long? {
            return null
        }

        override suspend fun fetchAll(): List<Document> {
            return emptyList()
        }

    }

}
