/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.managers;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.serhii.core.log.Log;

/**
 * Listener for foreground / background transitions.
 */
public class AppForegroundListener implements LifecycleObserver {

    private static final String TAG = "AppForegroundListener";

    private static boolean isInForeground;

    public static boolean isInForeground() {
        return isInForeground;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onForegroundEnter() {
        Log.info(TAG, "onForegroundEnter()");
        isInForeground = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onForegroundExit() {
        Log.info(TAG, "onForegroundExit()");
        isInForeground = false;
    }
}
