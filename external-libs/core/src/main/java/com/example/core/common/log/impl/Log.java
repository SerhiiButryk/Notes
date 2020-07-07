package com.example.core.common.log.impl;

public interface Log {

    void info(String tag, String message);

    void error(String tag, String message);

    void setTag(String tag);
}
