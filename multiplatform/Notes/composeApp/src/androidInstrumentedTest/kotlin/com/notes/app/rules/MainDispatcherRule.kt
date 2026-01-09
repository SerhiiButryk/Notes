package com.notes.app.rules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// Added this rule to be able to override main android thread in tests
//    @get:Rule
//    val mainCoroutineRule = MainDispatcherRule()
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        // Sets the TestDispatcher as the Main dispatcher
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        // Resets the Main dispatcher to the original
        Dispatchers.resetMain()
    }
}