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

@RunWith(Suite::class)
@SuiteClasses(CipherTests::class, DatabaseTests::class)
class AllTests