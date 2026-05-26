package com.notes.app.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

/**
 * TODO: Update
 */
val email = ""
val pass = ""

/**
 * A test which should verify correct startup behavior
 */
fun MacrobenchmarkScope.testLaunchAndWait() {
    startActivityAndWait()
}

/**
 * A test which should verify correct login behavior
 */
fun MacrobenchmarkScope.testLogin() {

    startActivityAndWait()

    val isFirstLaunch = !device.wait(
        Until.hasObject(By.textContains("Welcome again")),
        10_000
    )

    if (isFirstLaunch) {

        device.wait(Until.hasObject(By.text("Welcome to Notes")), 10_000)

        device.findObject(By.text("Continue")).click()

        device.wait(Until.hasObject(By.text("Create a user")), 10_000)

        device.findObject(By.textContains("Have you already got your account")).click()

        device.wait(Until.hasObject(By.textContains("Welcome again")), 10_000)

        device.findObject(By.res("input_auth_1"))
            .setText(email)
    }

    device.findObject(By.res("input_auth_2"))
        .setText(pass)

    device.findObject(By.text("Continue")).click()

    device.wait(Until.hasObject(By.textContains("Search your notes")), 10_000)

    // Scroll the list

    device.swipe(
        500,
        1600,
        500,
        400,
        20
    )
}