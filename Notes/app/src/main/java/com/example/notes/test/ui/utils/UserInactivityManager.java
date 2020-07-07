package com.example.notes.test.ui.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.core.common.log.Log;
import com.example.notes.test.control.NativeBridge;

public class UserInactivityManager {

    private static final String TAG = "UserInactivityManager";

    private static int REQUEST_CODE = 1;

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

    public void initManager(Context context) {

        // Cancel and restart
        if (alarmManager != null) {
            cancelAlarm();
        }

        Log.info(TAG, "alarm is initialized with " + context);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, InactivityEventReceiver.class);
        inactivityIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);
    }

    public void scheduleAlarm() {

        Log.info(TAG, "inactivity alarm is scheduled TIME: " + System.currentTimeMillis());

        NativeBridge nativeBridge = new NativeBridge();

        int time = nativeBridge.getIdleLockTime();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + time, inactivityIntent);
    }

    public void cancelAlarm() {

        Log.info(TAG, "inactivity alarm is canceled ");

        alarmManager.cancel(inactivityIntent);
    }

}
