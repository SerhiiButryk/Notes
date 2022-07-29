/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes

import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.apps.notes.activities.NotesViewActivity
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AppTests {

    var scenario: ActivityScenario<NotesViewActivity>? = null

    @Before
    fun setup() {
        Log.i(TAG, "setup() IN")

        scenario = TestUtility.launchApp(userName, userPassword)

        Log.i(TAG, "setup() OUT")
    }

    @After
    fun teardown() {
        Log.i(TAG, "teardown() IN")
        scenario?.let {
            TestUtility.closeApp(it)
        }
        Log.i(TAG, "teardown() OUT")
    }

    @Test
    fun test01_BackUp() {
        Log.i(TAG, "test01_BackUp() IN")

        TestUtility.waitFor(2*1000)

        Log.i(TAG, "test01_BackUp() OUT")
    }

    companion object {
        private val TAG: String = AppTests::class.java.simpleName
        private val userName: String = "myUser"
        private val userPassword: String = "myPassword"
    }

}