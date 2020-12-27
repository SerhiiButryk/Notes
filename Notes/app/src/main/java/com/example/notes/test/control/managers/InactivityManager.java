package com.example.notes.test.control.managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.core.log.Log;
import com.example.notes.test.control.NativeBridge;
import com.example.notes.test.control.broadcast.InactivityEventReceiver;

public class InactivityManager implements LifecycleObserver {

    private static final String TAG = InactivityManager.class.getSimpleName();

    private static final int REQUEST_CODE = 1;

    private AlarmManager alarmManager;
    private PendingIntent inactivityIntent;

    private static InactivityManager instance;

    public static InactivityManager getInstance() {
        if (instance == null) {
            instance = new InactivityManager();
        }
        return instance;
    }

    private InactivityManager() {
    }

    private void initManager(Context context) {

        // Cancel and restart
        if (alarmManager != null) {
            cancelAlarm();
        }

//        Log.info(TAG, "alarm is initialized with " + context);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, InactivityEventReceiver.class);
        inactivityIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void scheduleAlarm() {

//        Log.info(TAG, "inactivity alarm is scheduled TIME: " + System.currentTimeMillis());

        NativeBridge nativeBridge = new NativeBridge();

        int time = nativeBridge.getIdleLockTime();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + time, inactivityIntent);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void cancelAlarm() {

//        Log.info(TAG, "inactivity alarm is canceled ");

        alarmManager.cancel(inactivityIntent);
    }

    public void onUserInteraction() {
        // Reschedule
        InactivityManager.getInstance().cancelAlarm();
        InactivityManager.getInstance().scheduleAlarm();
    }

    public void setLifecycle(Context context, Lifecycle lifecycle) {
        initManager(context);
        lifecycle.addObserver(this);
    }

}
