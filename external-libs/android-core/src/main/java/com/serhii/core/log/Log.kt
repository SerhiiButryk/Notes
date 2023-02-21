/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.log

/**
 * Class provides logging functionality.
 * It is replacement for android native logger.
 */
class Log {

    // Added for compatibility with Java code
    companion object {

        @JvmStatic
        fun init() {
            enableDetailedLogsForDebug()
        }

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

        /**
         * Tag for [com.serhii.core.log.Log] class
         */
        @JvmStatic
        var tag: String
            get() = LogImpl.tag
            set(tag) {
                LogImpl.tag = tag
            }

        @JvmStatic
        fun enableDetailedLogs(isEnabled: Boolean) {
            LogImpl.setDetailedLogs(isEnabled)
        }

        /**
         * Enable detailed logs if it's debug build.
         * No-op if it's release build.
         */
        @JvmStatic
        fun enableDetailedLogsForDebug() {
            LogImpl.setDetailedLogsForDebug()
        }
    }

}