package com.example.core.log;

/**
 *   Library implementation for an app module usage
 *
 *   Usage of the android logger directly is prohibited
 *
 */

public class Log {

    public static void info(String tag, String message) {
        LogImpl.getInstance().info(tag, message);
    }

    public static void error(String tag, String message) {
        LogImpl.getInstance().error(tag, message);
    }

    public static void setTag(String tag) {
        LogImpl.getInstance().setTag(tag);
    }

}
