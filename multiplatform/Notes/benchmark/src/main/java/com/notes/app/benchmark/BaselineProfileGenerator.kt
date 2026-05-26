package com.notes.app.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a baseline profile
 *
 * ```
 * Preconditions:
 *
 * 1) UNCOMMENT
 *
 * <!--  Uncomment to enable benchmark testing -->
 * <!--        <profileable-->
 * <!--            android:shell="true"-->
 * <!--            tools:targetApi="29" />-->
 *
 *  2) Set to true
 *
 *  // gradle.properties
 *  "baseline.profile.tests.generator=true"
 *
 *  3) AND SET EMAIL AND PASS
 *
 *  Run:
 *
 *  $./gradlew :androidApp:generateBaselineProfile
 *
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        val packageName = "com.notes.app"
        rule.collect(
            packageName = packageName,
            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            includeInStartupProfile = true
        ) {
            pressHome()
            testLogin()
        }
    }
}