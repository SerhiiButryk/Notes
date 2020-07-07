package com.example.notes.test.common;

public class AppUtils {

    public static final String RUNTIME_LIBRARY = "rabbit";
    public static final String VERSION_LIBRARY = "1.0.0";

    public static String formatString(String message, int placeValue) {
        return message.replace('%', String.valueOf(placeValue).charAt(0));
    }
}
