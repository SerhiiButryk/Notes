package com.serhii.core.utils;

import android.content.Context;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *  Library helper functions
 */

public class GoodUtils {

    private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Message buildMessage(int what) {
        Message ms = Message.obtain();
        ms.what = what;
        return ms;
    }

    public static Message buildMessage(int what, Object obj) {
        Message ms = Message.obtain();
        ms.what = what;
        ms.obj = obj;
        return ms;
    }

    /**
     *  Retrieve an absolute file path for storing files in the internal app storage
     */
    public static String getFilePath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     *  Retrieve a text from a EditText widget
     */
    public static String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    /**
     *  Replace a place holder symbol with int value
     */
    public static String formatString(String message, int placeValue) {
        return message.replace("%", String.valueOf(placeValue));
    }

    public static String formatString(String message, String placeValue) {
        return message.replace("%", placeValue);
    }

    /**
     * Returns current timestamp in the format yyyy-MM-dd HH:mm:ss
     */
    public static String currentTimeToString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Show toast helper function
     */
    public static void showToast(Context context, int stringId) {
        Toast.makeText(context, context.getString(stringId), Toast.LENGTH_SHORT).show();
    }

}
