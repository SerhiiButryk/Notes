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

/**
 * UnitTests for testing main app functionality
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class BasicTests {

    companion object {
        private val TAG: String = BasicTests::class.java.simpleName
        private const val userName: String = "myemail@gmail.com"
        private const val userPassword: String = "myPassword"

        var scenario: ActivityScenario<NotesViewActivity>? = null

        @BeforeClass
        @JvmStatic
        fun setup() {
            Log.i(TAG, "setup() IN")
// TODO: Fix app login
            // Disable because it doesn work
//            scenario = TestUtility.launchApp(userName, userPassword)
            Log.i(TAG, "setup() OUT")
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            Log.i(TAG, "teardown() IN")
//            scenario?.let {
//                TestUtility.closeApp(it)
//            }
//            scenario = null
            Log.i(TAG, "teardown() OUT")
        }
    }

}