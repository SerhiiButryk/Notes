package com.example.notes.test.ui.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.core.common.log.Log;
import com.example.notes.test.control.NativeBridge;

public class UserInactivityManager implements LifecycleObserver {

    private static final String TAG = "UserInactivityManager";

    private AlarmManager alarmManager;
    private PendingIntent inactivityIntent;

    private static UserInactivityManager instance;

    public static UserInactivityManager getInstance() {
        if (instance == null) {
            instance = new UserInactivityManager();
        }
        return instance;
    }

    private UserInactivityManager() {
    }

    private void initManager(Context context) {

        // Cancel and restart
        if (alarmManager != null) {
            cancelAlarm();
        }

        Log.info(TAG, "alarm is initialized with " + context);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, InactivityEventReceiver.class);
        inactivityIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void scheduleAlarm() {

        Log.info(TAG, "inactivity alarm is scheduled TIME: " + System.currentTimeMillis());

        NativeBridge nativeBridge = new NativeBridge();

        int time = nativeBridge.getIdleLockTime();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + time, inactivityIntent);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void cancelAlarm() {

        Log.info(TAG, "inactivity alarm is canceled ");

        alarmManager.cancel(inactivityIntent);
    }

    public void onUserInteraction() {
        // Reschedule
        UserInactivityManager.getInstance().cancelAlarm();
        UserInactivityManager.getInstance().scheduleAlarm();
    }

    public void setLifecycle(Context context, Lifecycle lifecycle) {
        initManager(context);
        lifecycle.addObserver(this);
    }

}
