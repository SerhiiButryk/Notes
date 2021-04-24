package com.serhii.core.log;

/**
 *   Class provides logging functionality.
 *
 *   Usage of the android logger directly is prohibited.
 */

public class Log {

    public static void info(String tag, String message) {
        LogImpl.getInstance().info(tag, message);
    }

    public static void detail(String tag, String message) {
        LogImpl.getInstance().detail(tag, message);
    }

    public static void error(String tag, String message) {
        LogImpl.getInstance().error(tag, message);
    }

    public static void setTag(String tag) {
        LogImpl.getInstance().setTag(tag);
    }

    public static String getTag() {
        return LogImpl.getInstance().getTag();
    }

    public static void setDetailedLogs(boolean isEnabled) {
        LogImpl.setDetailedLogs(isEnabled);
    }

}
