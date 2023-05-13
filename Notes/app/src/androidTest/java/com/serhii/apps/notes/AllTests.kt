/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses
import com.serhii.apps.notes.CipherTests
import com.serhii.apps.notes.DatabaseTests

/**
 * Suite contains all available tests for the app
 */
@RunWith(Suite::class)
@SuiteClasses(
    CipherTests::class,
    DatabaseTests::class,
    AppTests::class
)
class AllTests