package com.notes.app

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import api.PlatformAPIs
import api.data.AbstractStorageService
import api.data.Document
import api.data.toDocument
import api.data.toJson
import com.google.common.truth.Truth.assertThat
import com.notes.app.security.CryptoProvider
import api.data.EncryptedStore
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasicTests {

    private val appContext: Context = InstrumentationRegistry
        .getInstrumentation().targetContext.applicationContext

    private val mockedStoreService = MockedService()

    class MockedService : AbstractStorageService() {

        private val list = mutableListOf<Document>()

        override suspend fun store(document: Document): Boolean {
            list.add(document)
            return true
        }

        override suspend fun load(name: String): Document? {
            for (document in list) {
                if (document.name == name) {
                    return document
                }
            }
            return null
        }

        override suspend fun delete(name: String): Boolean {
            return true
        }

        override suspend fun fetchAll(): List<Document> {
            return list
        }

        fun clear() {
            list.clear()
        }

        override val name: String = ""
    }

    @Before
    fun onStart() {
    }

    @After
    fun onFinish() {
    }

    @Test
    fun test01_encryptDecryptWithDerivedKey() = runTest {

        val crypto = CryptoProvider()

        val key = PlatformAPIs.storage.get("derived_key_pass")
        assertThat(key.isEmpty()).isTrue()

        crypto.onLoginCompleted("hellohellohello", "reitueoirrngeorg490904r3094rjr209jowmf.a,c2")

        val key2 = PlatformAPIs.storage.get("derived_key_pass")
        assertThat(key2.isEmpty()).isFalse()

        val message = "hello world"
        val ciphertext = crypto.encryptWithDerivedKey(message)

        assertThat(ciphertext.isEmpty()).isFalse()

        val plaintext = crypto.decryptWithDerivedKey(ciphertext)

        assertThat(plaintext).isEqualTo(message)

        // Clear derived key
        PlatformAPIs.storage.clearAll()
    }

    @Test
    fun test02_encryptedStore() = runTest {

        PlatformAPIs.crypto.onLoginCompleted("test", "test") // Gen derived key

        val doc1 = Document("doc1", "data 1")

        val encryptedStore = EncryptedStore(mockedStoreService)

        var res = encryptedStore.store(doc1)
        assertThat(res).isTrue()

        val retDoc = encryptedStore.load("doc1")

        assertThat(retDoc).isNotNull()
        assertThat(retDoc).isEqualTo(doc1)

        mockedStoreService.clear()

        val docList = listOf(Document("doc1", "data 1"), Document("doc2", "data 2"))

        res = encryptedStore.store(docList[0])

        assertThat(res).isTrue()

        res = encryptedStore.store(docList[1])

        assertThat(res).isTrue()

        val docListRet = encryptedStore.fetchAll()

        assertThat(docListRet).isEqualTo(docList)

        // Clear derived key
        PlatformAPIs.storage.clearAll()

    }

    @Test
    fun test03_jsonTest() = runTest {

        val doc = Document("name1", "data1")

        val expected = """{"name":"name1","content":"data1"}"""

        val result = doc.toJson()
        assertThat(result).isNotEmpty()
        assertThat(result).isEqualTo(expected)

        val docExpected = Document("name1", "content1")
        val json = """{"name":"name1", "content":"content1"}"""
        val json2 = """{"content":"content1","name":"name1"}"""

        val doc1 = json.toDocument()
        assertThat(doc1).isEqualTo(docExpected)

        val doc2 = json2.toDocument()
        assertThat(doc2).isEqualTo(docExpected)

        val doc3 = Document("", "")
        val expected2 = """{"name":"","content":""}"""

        val result2 = doc3.toJson()
        assertThat(result2).isNotEmpty()
        assertThat(result2).isEqualTo(expected2)

        val json3 = """"""
        val json4 = """{}"""
        val json5 = """{"keyUnknown":""}"""

        // Just to check that we don't crash
        val doc4 = json3.toDocument()
        assertThat(doc4).isEqualTo(Document("", ""))
        val doc5 = json4.toDocument()
        assertThat(doc5).isEqualTo(Document("", ""))
        val doc6 = json5.toDocument()
        assertThat(doc6).isEqualTo(Document("", ""))
    }

}