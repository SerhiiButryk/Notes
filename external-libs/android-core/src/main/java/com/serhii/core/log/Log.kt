/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.log

/**
 * Public class which provides logging functionality.
 * It's a replacement for android native logger.
 */
class Log {

    // Added 'JvmStatic' annotation for compatibility with Java code
    companion object {

        init {
            LogImpl.init()
        }

        @JvmStatic
        fun info(tag: String = "", message: String) {
            LogImpl.info(tag, message)
        }

        @JvmStatic
        fun detail(tag: String = "", message: String) {
            LogImpl.detail(tag, message)
        }

        @JvmStatic
        fun error(tag: String = "", message: String) {
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
        fun enableDetailedLogs(enabled: Boolean) {
            LogImpl.setDetailedLogs(enabled)
        }

        @JvmStatic
        fun setVersionCode(versionCode: String) {
            LogImpl.setVersionCode(versionCode)
        }
    }
}