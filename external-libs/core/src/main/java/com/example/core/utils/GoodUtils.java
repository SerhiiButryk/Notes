package com.example.core.utils;

import android.content.Context;
import android.os.Message;
import android.widget.TextView;

/**
 *  Library helper functions
 */

public class GoodUtils {

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
     *  Retrieve a text from a TextView widget
     */
    public static String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

}
