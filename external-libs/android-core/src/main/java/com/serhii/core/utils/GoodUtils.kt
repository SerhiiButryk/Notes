/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Library helper functions
 */

private const val DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm"

/**
 * Returns current timestamp in the format yyyy-MM-dd HH:mm:ss
 */
fun currentTimeToString(): String {
    val dateFormat = SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.getDefault())
    return dateFormat.format(Date())
}