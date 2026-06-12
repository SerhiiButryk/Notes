package com.notes.app

import api.AppService
import api.AppServices
import api.Platform
import com.google.common.truth.Truth.assertThat
import com.notes.os.JVMInitProvider
import com.notes.os.impl.CryptoKeyStore
import kotlinx.coroutines.test.runTest
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.BeforeTest
import kotlin.test.Test

class BasicTests {

    @OptIn(ExperimentalAtomicApi::class)
    private val initialized = AtomicBoolean(false)

    @BeforeTest
    fun setup() {
        ensurePlatformCreated()
    }

    @Test
    fun test01_keystore_encrypt_decrypt() = runTest {

        val pass = "hasdsdwowdkwdwodk12718"
        val cryptoKeyStore = CryptoKeyStore(pass.toCharArray())

        assertThat(cryptoKeyStore.testOnly_getKeystoreFile().exists()).isTrue()

        val text = "hello"
        val cipherText = cryptoKeyStore.encrypt(text)

        val plainText = cryptoKeyStore.decrypt(cipherText)

        assertThat(plainText).isEqualTo(text)

        cryptoKeyStore.onDestroy()

        ensurePlatformDestroyed()
    }

    @Test
    fun test02_crypto_encrypt_decrypt() = runTest {

        val crypto = Platform().crypto

        val pass = "hasdsdwowdkwdwodk12718"
        val email = "eywfyu@gmail"

        crypto.onAuthCompleted(password = pass, uid = email)

        val text = """
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

        ensurePlatformDestroyed()

    }

    @OptIn(ExperimentalAtomicApi::class)
    private fun ensurePlatformCreated() {
        if (initialized.compareAndSet(false, true)) {
            JVMInitProvider.create()
        }
    }

    private suspend fun ensurePlatformDestroyed() {
        AppServices.getServiceByKey(AppService.FIREBASE_MAIN).onDestroy()
    }

}