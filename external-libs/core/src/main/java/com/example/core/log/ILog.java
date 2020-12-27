package com.example.core.log;

interface ILog {

    void info(String tag, String message);

    void error(String tag, String message);

    void setTag(String tag);
}
