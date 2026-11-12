package com.notes.app

import api.AppService
import api.AppServices
import api.Platform
import com.notes.os.JVMInitProvider
import com.notes.repo.JvmSyncManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

open class BaseTest {

    @OptIn(ExperimentalAtomicApi::class)
    companion object {

        private val initialized = AtomicBoolean(false)

        @JvmStatic
        @BeforeClass
        fun setup() {
            ensurePlatformCreated()
        }

        @JvmStatic
        @AfterClass
        fun destroy() {
            ensurePlatformDestroyed()
        }

        @OptIn(ExperimentalAtomicApi::class)
        private fun ensurePlatformCreated() {
            if (initialized.compareAndSet(false, true)) {
                JVMInitProvider.onCreate()
            }
        }

        @OptIn(ExperimentalAtomicApi::class, DelicateCoroutinesApi::class)
        private fun ensurePlatformDestroyed() {
            GlobalScope.launch {
                AppServices.getServiceByKey(AppService.FIREBASE_MAIN)?.onDestroy()
                initialized.store(false)
            }
        }

    }

}