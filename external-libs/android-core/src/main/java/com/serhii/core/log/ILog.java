package com.serhii.core.log;

interface ILog {

    void info(String tag, String message);

    void detail(String tag, String message);

    void error(String tag, String message);

    void setTag(String tag);

    String getTag();
}
