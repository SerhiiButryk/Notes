/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes

import android.content.Intent
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.serhii.apps.notes.activities.NotesViewActivity
import java.io.BufferedInputStream
import java.io.InputStream

object TestUtility {

    /**
     * Read file in assets and returns empty string in case of errors
     */
    fun readFileFromTestAssets(filename: String) : String {
        val context = InstrumentationRegistry.getInstrumentation().context
        var text = ""
        var inputStream : InputStream? = null
        try {
            inputStream = context.assets.open(filename)
            inputStream = BufferedInputStream(inputStream)
            Log.i(
                TAG, "readFileFromTestAssets(), going to read ${inputStream.available()}" +
                        "bytes in file $filename")

            val byteArray = ByteArray(1024)
            while (inputStream.read(byteArray) != -1) {
                text += String(byteArray)
            }

        } catch (e: Exception) {
            Log.i(TAG, "readFileFromTestAssets(), error: $e")
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return text
    }

    /**
     * Launches and logs in app
     */
    fun launchApp(userName: String, userPassword: String): ActivityScenario<NotesViewActivity> {
        // Launch app
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, NotesViewActivity::class.java)
        val scenario: ActivityScenario<NotesViewActivity> = ActivityScenario.launch(intent)

        // Make a little pause
        waitFor(3*1000)

        login(userName, userPassword)

        return scenario
    }

    /**
     * Logs in app
     */
    fun login(userName: String, userPassword: String) {

        val isUserCreated = isViewDisplayed(R.id.btn_login)

        if (!isUserCreated) {

            // Continue
            Espresso.onView(ViewMatchers.withId(R.id.btn_register))
                .perform(ViewActions.click())

            // Register
            Espresso.onView(ViewMatchers.withId(R.id.usr_email))
                .perform(ViewActions.typeText(userName))

            Espresso.onView(ViewMatchers.withId(R.id.usr_password))
                .perform(ViewActions.typeText(userPassword))

            Espresso.onView(ViewMatchers.withId(R.id.confirm_password))
                .perform(ViewActions.typeText(userPassword))

            Espresso.closeSoftKeyboard()

            // Make a little pause
            waitFor(1 * 1000)

            Espresso.onView(ViewMatchers.withId(R.id.btn_register))
                .perform(ViewActions.click())

        }

        // Login
        Espresso.onView(ViewMatchers.withId(R.id.input_password))
            .perform(ViewActions.typeText(userPassword))

        Espresso.closeSoftKeyboard()

        // Make a little pause
        waitFor(1*1000)

        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .perform(ViewActions.click())

    }

    /**
     * Runs shell command
     */
    fun runShellCommand(command: String) {
        InstrumentationRegistry.getInstrumentation()
            .getUiAutomation().executeShellCommand(command)
    }

    /**
     * Closes app activity
     */
    fun closeApp(scenario: ActivityScenario<NotesViewActivity>) {
        scenario.close()
    }

    /**
     * Checks if View is present on a screen
     * return false if view is NOT displayed, otherwise true
     */
    fun isViewDisplayed(id: Int): Boolean {
        var isDisplayed = true
        try {
            Espresso.onView(ViewMatchers.withId(id))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        } catch (e: Throwable) {
            Log.w(TAG, "Got exception: $e")
            e.printStackTrace()
            isDisplayed = false
        }
        return isDisplayed
    }

    /**
     * Helper function to wait for specified time
     */
    fun waitFor(milliseconds: Long) {
        Log.i(TAG, "waitFor() waiting for $milliseconds")
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            Log.i(TAG, "waitFor() Failed to wait for $milliseconds millis, error: $e")
            e.printStackTrace()
        }
    }

    private val TAG = TestUtility::class.java.simpleName

}