package com.notes.app

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import api.PlatformAPIs
import com.google.common.truth.Truth.assertThat
import com.notes.app.security.CryptoProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@RunWith(AndroidJUnit4::class)
class BasicTests {

    val appContext: Context = InstrumentationRegistry
        .getInstrumentation().targetContext.applicationContext

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
    }
}