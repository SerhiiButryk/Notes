package com.notes.app.benchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmarks for the app.
 *
 * Preconditions:
 *
 * 1) UNCOMMENT
 *
 * <!--  Uncomment to enable benchmark testing -->
 * <!--        <profileable-->
 * <!--            android:shell="true"-->
 * <!--            tools:targetApi="29" />-->
 *
 *  2) Set to false
 *
 *  // gradle.properties
 *  "baseline.profile.tests.generator=false"
 *
 *  3) AND SET EMAIL AND PASS
 */
@RunWith(AndroidJUnit4::class)
class BenchmarksBasic {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val metrics = listOf(
        StartupTimingMetric()
    )

    private val iterations = 1

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.notes.app",
        metrics = metrics,
        iterations = iterations,
        startupMode = StartupMode.COLD,
        setupBlock = {
            pressHome()
        },
    ) {
        testLogin()
    }
}