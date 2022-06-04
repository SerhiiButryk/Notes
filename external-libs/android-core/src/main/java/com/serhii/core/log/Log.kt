/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.log

/**
 * Class provides logging functionality.
 * Usage of the android logger directly is prohibited.
 */
class Log {

    // TODO: Replace with object declaration
    // Added for compatibility with Java code
    companion object {

        @JvmStatic
        fun info(tag: String, message: String) {
            LogImpl.info(tag, message)
        }

        @JvmStatic
        fun detail(tag: String, message: String) {
            LogImpl.detail(tag, message)
        }

        @JvmStatic
        fun error(tag: String, message: String) {
            LogImpl.error(tag, message)
        }

        @JvmStatic
        var tag: String
            get() = LogImpl.tag
            set(tag) {
                LogImpl.tag = tag
            }

        @JvmStatic
        fun setDetailedLogs(isEnabled: Boolean) {
            LogImpl.setDetailedLogs(isEnabled)
        }

    }

}