package com.notes.app.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 *
 * Launches tests and pre-compiles app sources using information from
 * baseline profiler file.
 *
 * Use:
 *
 * CompilationMode.None() - For runs without optimizations
 *
 * CompilationMode.Full() - For runs with full optimizations.
 * If we want to measure full runtime performance this can be used
 *
 * CompilationMode.Partial() - For runs with partial optimizations
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
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BenchmarksAdvance {

    @get:Rule
    val rule = MacrobenchmarkRule()

    private val iterations = 1

    private val metrics = listOf(
        StartupTimingMetric(),
        FrameTimingMetric(),
    )

    @Test
    fun startupCompilationNone() =
        runTestBenchmark(CompilationMode.None())

    @Test
    fun startupCompilationBaselineProfiles() =
        runTestBenchmark(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun runTestBenchmark(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = "com.notes.app",
            metrics = metrics,
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = iterations,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                testLogin()
            }
        )
    }
}