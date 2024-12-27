/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

/**
 * Run all tests for the app
 */
@RunWith(Suite::class)
@SuiteClasses(
    CryptoTests::class,
    DatabaseTests::class
)
class AllTests