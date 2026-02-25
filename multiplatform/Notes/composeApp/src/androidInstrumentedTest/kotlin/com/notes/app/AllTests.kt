package com.notes.app

import org.junit.runner.RunWith
import org.junit.runners.Suite

@Suite.SuiteClasses(
    BasicTests::class,
    DatabaseTest::class,
//    RepoTest::class,
//    ViewModelNotesTest::class,
)
@RunWith(Suite::class)
class AllTests