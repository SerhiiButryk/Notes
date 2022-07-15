package com.serhii.apps.notes.control.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.serhii.apps.notes.control.managers.AppForegroundListener;
import com.serhii.core.log.Log;
import com.serhii.apps.notes.activities.AuthorizationActivity;

import java.util.Date;

public class InactivityEventReceiver extends BroadcastReceiver {

    private static final String TAG = "InactivityEventReceiver";
    private static boolean isInactivityTimeoutReceived;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.detail(TAG, "onReceive(), received inactivity timeout, time: " + new Date(System.currentTimeMillis()));
        isInactivityTimeoutReceived = true;
        if (AppForegroundListener.isInForeground()) {
            Log.detail(TAG, "onReceive(), in foreground, start auth activity");
            startActivity(context);
        }
    }

    // Called from onResume() in NotesViewActivity and SettingsActivity activities
    public static void checkIfInactivityTimeoutReceived(Context context) {
        if (isInactivityTimeoutReceived) {
            Log.detail(TAG, "checkIfInactivityTimeoutReceived(), time out received, start auth activity");
            startActivity(context);
        }
    }

    private static void startActivity(Context context) {
        Intent startActivityIntent = new Intent(context, AuthorizationActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(startActivityIntent);
        // Reset flag
        isInactivityTimeoutReceived = false;
    }

}
