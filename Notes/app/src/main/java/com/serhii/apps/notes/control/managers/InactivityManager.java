package com.serhii.apps.notes.control.managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceManager;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.broadcast.InactivityEventReceiver;
import com.serhii.core.log.Log;

public class InactivityManager implements LifecycleObserver {

    private static final String TAG = InactivityManager.class.getSimpleName();

    private static final int REQUEST_CODE = 1;

    private AlarmManager alarmManager;
    private PendingIntent inactivityIntent;
    private int timeoutTimeMillis;

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

        if (context == null) {
            Log.error(TAG, "initManager(), passed context is null");
            throw new NullPointerException("initManager(), passed context is null");
        }

        // Cancel and restart
        if (alarmManager != null) {
            cancelAlarm();
        }

        Log.info(TAG, "initManager(), alarm is initialized with " + context);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, InactivityEventReceiver.class);
        inactivityIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);

        updateTimeout(context);
    }

    public void updateTimeout(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String timeDefault = context.getString(R.string.preference_idle_lock_time_default);
        String time = sharedPreferences.getString(context.getString(R.string.preference_idle_lock_timeout_key), timeDefault);

        Log.detail(TAG, "updateTimeout(), retrieved time: " + time);

        timeoutTimeMillis = Integer.parseInt(time);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void scheduleAlarm() {

        Log.detail(TAG, "inactivity alarm is scheduled TIME: " + System.currentTimeMillis());

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + timeoutTimeMillis, inactivityIntent);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void cancelAlarm() {

        Log.detail(TAG, "inactivity alarm is canceled ");

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
