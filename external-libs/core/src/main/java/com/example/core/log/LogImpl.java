package com.example.core.log;

import com.example.core.CoreEngine;

class LogImpl implements ILog {

    private static String TAG;
    private static String DELIMITER = " ";

    private static LogImpl instance;

    public static ILog getInstance() {
        if (instance == null) {
            instance = new LogImpl();
        }
        return instance;
    }

    private LogImpl() {
        setTag("_LOGTAG_"); // Default tag
    }

    @Override
    public void info(String tag, String message) {
        android.util.Log.i(TAG + DELIMITER +  tag + DELIMITER, message);
    }

    @Override
    public void error(String tag, String message) {
        android.util.Log.e(TAG + DELIMITER +  tag + DELIMITER, message);
    }

    @Override
    public void setTag(String tag) {
        TAG = tag;
        _setTag(tag);
    }

    static {
        System.loadLibrary(CoreEngine.RUNTIME_LIBRARY);
    }

    private native void _setTag(String tag);

}
